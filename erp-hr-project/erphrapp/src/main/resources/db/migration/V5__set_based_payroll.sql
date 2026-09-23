/* =====================================================================
   V5  --  sp_run_payroll rewritten set-based (review, performance table)

   The V1 version walked employees with a cursor: ~8 INSERTs and 3 function
   calls per employee inside one transaction. This version computes every
   employee of the period at once in a working table and writes each kind
   of payslip line with a single INSERT ... SELECT.

   Formulas, column types and rounding are identical to V1 (the working
   table columns use the same DECIMAL types as the old variables), and
   PayrollGoldenMasterTest checks both versions produce the same payslips,
   lines, loan installments and loan balances.

   MySQL can't open the same TEMPORARY table twice in one statement, so
   intermediate results that need a second pass get their own temp table.
   ===================================================================== */

DROP PROCEDURE IF EXISTS sp_run_payroll;

DELIMITER $$
CREATE PROCEDURE sp_run_payroll(IN p_period_code CHAR(7))
BEGIN
  DECLARE v_period_id  INT;
  DECLARE v_year       SMALLINT;
  DECLARE v_start      DATE;
  DECLARE v_end        DATE;
  DECLARE v_wdays      TINYINT;
  DECLARE v_status     VARCHAR(10);
  DECLARE v_dhours     DECIMAL(4,1);
  DECLARE v_ot_factor  DECIMAL(4,2);
  DECLARE v_r_emp      DECIMAL(5,2);
  DECLARE v_r_er       DECIMAL(5,2);
  DECLARE v_ins_min    DECIMAL(12,2);
  DECLARE v_ins_max    DECIMAL(12,2);
  DECLARE v_exempt     DECIMAL(14,2);

  SELECT period_id, fiscal_year, start_date, end_date, working_days, status
    INTO v_period_id, v_year, v_start, v_end, v_wdays, v_status
    FROM payroll_periods WHERE period_code = p_period_code;

  IF v_period_id IS NULL THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Payroll period not found';
  END IF;

  /* guard lives here, not only in Java: approved/paid/closed runs must never be wiped */
  IF v_status NOT IN ('OPEN','PROCESSED') THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Payroll can only be run on an OPEN or PROCESSED period';
  END IF;

  /* deleting payslips would cascade into payroll_payments */
  IF EXISTS (SELECT 1 FROM payroll_payments pp
               JOIN payslips p ON p.payslip_id = pp.payslip_id
              WHERE p.period_id = v_period_id) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Payroll period already has payment records';
  END IF;

  SELECT daily_hours, overtime_factor, ins_employee_rate, ins_employer_rate,
         ins_min_wage, ins_max_wage, personal_exemption
    INTO v_dhours, v_ot_factor, v_r_emp, v_r_er, v_ins_min, v_ins_max, v_exempt
    FROM payroll_settings WHERE fiscal_year = v_year;

  /* re-run safe: roll back loan installments, then wipe the old run */
  UPDATE loans l
    JOIN loan_installments li ON li.loan_id = l.loan_id AND li.period_code = p_period_code
     SET l.remaining_balance = l.remaining_balance + li.amount,
         l.status = IF(l.status = 'CANCELLED', 'CANCELLED', 'ACTIVE');  -- never revive a cancelled loan
  DELETE FROM loan_installments WHERE period_code = p_period_code;
  DELETE FROM payslips WHERE period_id = v_period_id;

  /* ---- 0. who is paid, and their inputs --------------------------- */
  DROP TEMPORARY TABLE IF EXISTS tmp_run, tmp_tax, tmp_loan;
  CREATE TEMPORARY TABLE tmp_run (
    emp_id      INT PRIMARY KEY,
    payslip_id  BIGINT NULL,
    basic       DECIMAL(12,2) NOT NULL,
    prorate     DECIMAL(6,4)  NOT NULL,
    ot_hours    DECIMAL(6,2)  NOT NULL,
    abs_days    DECIMAL(4,1)  NOT NULL,
    present     DECIMAL(4,1)  NOT NULL,
    gross       DECIMAL(12,2) NOT NULL DEFAULT 0,
    taxable     DECIMAL(12,2) NOT NULL DEFAULT 0,
    ins_wage    DECIMAL(12,2) NOT NULL DEFAULT 0,
    ins_emp     DECIMAL(12,2) NOT NULL DEFAULT 0,
    ins_er      DECIMAL(12,2) NOT NULL DEFAULT 0,
    annual      DECIMAL(14,2) NOT NULL DEFAULT 0,
    tax         DECIMAL(12,2) NOT NULL DEFAULT 0
  ) ENGINE=InnoDB;

  -- basic = latest ACTIVE contract covering the last day of the period (same rule as fn_current_basic)
  INSERT INTO tmp_run (emp_id, basic, prorate, ot_hours, abs_days, present)
  SELECT e.emp_id,
         c.basic_salary,
         IF(e.hire_date > v_start, (DATEDIFF(v_end, e.hire_date) + 1) / (DATEDIFF(v_end, v_start) + 1), 1.0000),
         IFNULL(a.overtime_hours, 0),
         IFNULL(a.unpaid_absent_days, 0),
         IF(a.att_id IS NULL, v_wdays, IFNULL(a.present_days, 0))
    FROM employees e
    JOIN (SELECT emp_id, basic_salary,
                 ROW_NUMBER() OVER (PARTITION BY emp_id ORDER BY start_date DESC) AS rn
            FROM employee_contracts
           WHERE status = 'ACTIVE' AND start_date <= v_end
             AND (end_date IS NULL OR end_date >= v_end)) c
      ON c.emp_id = e.emp_id AND c.rn = 1 AND c.basic_salary > 0
    LEFT JOIN attendance_summary a ON a.period_id = v_period_id AND a.emp_id = e.emp_id
   WHERE (e.emp_status IN ('ACTIVE','PROBATION','SUSPENDED') OR e.termination_date IS NOT NULL)
     AND e.hire_date <= v_end
     AND (e.termination_date IS NULL OR e.termination_date >= v_start);

  /* ---- payslip headers --------------------------------------------- */
  INSERT INTO payslips (payslip_no, period_id, emp_id, basic_salary, worked_days, absent_days, overtime_hours, status)
  SELECT CONCAT('PS-', REPLACE(p_period_code,'-',''), '-', LPAD(t.emp_id,4,'0')),
         v_period_id, t.emp_id, ROUND(t.basic * t.prorate, 2), t.present, t.abs_days, t.ot_hours, 'DRAFT'
    FROM tmp_run t ORDER BY t.emp_id;

  UPDATE tmp_run t JOIN payslips p ON p.period_id = v_period_id AND p.emp_id = t.emp_id
     SET t.payslip_id = p.payslip_id;

  /* ---- 1. basic ------------------------------------------------------ */
  INSERT INTO payslip_lines (payslip_id, comp_id, comp_code, comp_name_ar, comp_type, amount, is_taxable, calc_note, print_order)
  SELECT t.payslip_id, sc.comp_id, sc.code, sc.name_ar, 'EARNING',
         ROUND(t.basic * t.prorate, 2), sc.is_taxable,
         IF(t.prorate < 1, CONCAT('Pro-rated ', ROUND(t.prorate*100,1), '%'), 'Full month'), sc.print_order
    FROM tmp_run t JOIN salary_components sc ON sc.code = 'BASIC';

  /* ---- 2. recurring earnings ----------------------------------------- */
  INSERT INTO payslip_lines (payslip_id, comp_id, comp_code, comp_name_ar, comp_type, amount, is_taxable, calc_note, print_order)
  SELECT t.payslip_id, sc.comp_id, sc.code, sc.name_ar, 'EARNING',
         ROUND(CASE sc.calc_type
                 WHEN 'PCT_BASIC' THEN t.basic * IFNULL(esc.percentage, sc.default_value) / 100
                 ELSE IFNULL(esc.amount, sc.default_value)
               END * t.prorate, 2),
         sc.is_taxable,
         CASE sc.calc_type
           WHEN 'PCT_BASIC' THEN CONCAT(IFNULL(esc.percentage, sc.default_value), '% of basic')
           ELSE 'Fixed amount' END,
         sc.print_order
    FROM tmp_run t
    JOIN employee_salary_components esc ON esc.emp_id = t.emp_id
    JOIN salary_components sc ON sc.comp_id = esc.comp_id
   WHERE sc.comp_type = 'EARNING' AND sc.calc_type <> 'COMPUTED' AND sc.is_active = 1
     AND esc.effective_from <= v_end
     AND (esc.effective_to IS NULL OR esc.effective_to >= v_start);

  /* ---- 3. overtime ----------------------------------------------------- */
  INSERT INTO payslip_lines (payslip_id, comp_id, comp_code, comp_name_ar, comp_type, amount, is_taxable, calc_note, print_order)
  SELECT t.payslip_id, sc.comp_id, sc.code, sc.name_ar, 'EARNING',
         ROUND((t.basic / v_wdays / v_dhours) * v_ot_factor * t.ot_hours, 2), sc.is_taxable,
         CONCAT(t.ot_hours, ' hrs x ', v_ot_factor), sc.print_order
    FROM tmp_run t JOIN salary_components sc ON sc.code = 'OT'
   WHERE t.ot_hours > 0;

  /* ---- 4. gross & taxable base, 5. social insurance -------------------- */
  UPDATE tmp_run t
    JOIN (SELECT payslip_id, IFNULL(SUM(amount),0) AS gross, IFNULL(SUM(IF(is_taxable = 1, amount, 0)),0) AS taxable
            FROM payslip_lines WHERE comp_type = 'EARNING' GROUP BY payslip_id) g
      ON g.payslip_id = t.payslip_id
     SET t.gross = g.gross, t.taxable = g.taxable;

  UPDATE tmp_run
     SET ins_wage = LEAST(GREATEST(basic, v_ins_min), v_ins_max);
  UPDATE tmp_run
     SET ins_emp = ROUND(ins_wage * v_r_emp / 100, 2),
         ins_er  = ROUND(ins_wage * v_r_er  / 100, 2);

  INSERT INTO payslip_lines (payslip_id, comp_id, comp_code, comp_name_ar, comp_type, amount, is_taxable, calc_note, print_order)
  SELECT t.payslip_id, sc.comp_id, sc.code, sc.name_ar, 'DEDUCTION', t.ins_emp, 0,
         CONCAT(v_r_emp, '% of ', t.ins_wage), sc.print_order
    FROM tmp_run t JOIN salary_components sc ON sc.code = 'INS';

  /* ---- 6. income tax (same arithmetic as fn_income_tax, all employees at once) */
  UPDATE tmp_run
     SET taxable = GREATEST(0, taxable - ins_emp);
  UPDATE tmp_run
     SET annual = GREATEST(0, (taxable * 12) - IFNULL(v_exempt, 0));

  CREATE TEMPORARY TABLE tmp_tax (emp_id INT PRIMARY KEY, annual_tax DECIMAL(14,2) NOT NULL) ENGINE=InnoDB;
  INSERT INTO tmp_tax (emp_id, annual_tax)
  SELECT t.emp_id,
         CAST(IFNULL(SUM(GREATEST(0, LEAST(t.annual, b.to_amount) - b.from_amount) * b.rate / 100), 0) AS DECIMAL(14,2)) AS annual_tax
    FROM tmp_run t LEFT JOIN tax_brackets b ON b.fiscal_year = v_year
   GROUP BY t.emp_id;

  UPDATE tmp_run t JOIN tmp_tax x ON x.emp_id = t.emp_id
     SET t.tax = ROUND(x.annual_tax / 12, 2);

  INSERT INTO payslip_lines (payslip_id, comp_id, comp_code, comp_name_ar, comp_type, amount, is_taxable, calc_note, print_order)
  SELECT t.payslip_id, sc.comp_id, sc.code, sc.name_ar, 'DEDUCTION', t.tax, 0,
         CONCAT('Progressive on ', t.taxable), sc.print_order
    FROM tmp_run t JOIN salary_components sc ON sc.code = 'TAX'
   WHERE t.tax > 0;

  /* ---- 7a. unpaid absence ------------------------------------------------ */
  INSERT INTO payslip_lines (payslip_id, comp_id, comp_code, comp_name_ar, comp_type, amount, is_taxable, calc_note, print_order)
  SELECT t.payslip_id, sc.comp_id, sc.code, sc.name_ar, 'DEDUCTION',
         ROUND((t.gross / v_wdays) * t.abs_days, 2), 0,
         CONCAT(t.abs_days, ' unpaid day(s)'), sc.print_order
    FROM tmp_run t JOIN salary_components sc ON sc.code = 'ABS'
   WHERE t.abs_days > 0;

  /* ---- 7b. other recurring deductions ------------------------------------ */
  INSERT INTO payslip_lines (payslip_id, comp_id, comp_code, comp_name_ar, comp_type, amount, is_taxable, calc_note, print_order)
  SELECT t.payslip_id, sc.comp_id, sc.code, sc.name_ar, 'DEDUCTION',
         ROUND(CASE sc.calc_type
                 WHEN 'PCT_BASIC' THEN t.basic * IFNULL(esc.percentage, sc.default_value) / 100
                 ELSE IFNULL(esc.amount, sc.default_value) END, 2),
         0,
         CASE sc.calc_type WHEN 'PCT_BASIC' THEN 'Percentage of basic' ELSE 'Fixed amount' END,
         sc.print_order
    FROM tmp_run t
    JOIN employee_salary_components esc ON esc.emp_id = t.emp_id
    JOIN salary_components sc ON sc.comp_id = esc.comp_id
   WHERE sc.comp_type = 'DEDUCTION' AND sc.calc_type <> 'COMPUTED' AND sc.is_active = 1
     AND esc.effective_from <= v_end
     AND (esc.effective_to IS NULL OR esc.effective_to >= v_start);

  /* ---- 7c. loan installment: each employee's oldest active loan --------- */
  CREATE TEMPORARY TABLE tmp_loan (emp_id INT PRIMARY KEY, payslip_id BIGINT NOT NULL, loan_id INT NOT NULL, inst DECIMAL(12,2) NOT NULL) ENGINE=InnoDB;
  INSERT INTO tmp_loan (emp_id, payslip_id, loan_id, inst)
  SELECT t.emp_id, t.payslip_id, l.loan_id, l.inst
    FROM tmp_run t
    JOIN (SELECT emp_id, loan_id, LEAST(monthly_installment, remaining_balance) AS inst,
                 ROW_NUMBER() OVER (PARTITION BY emp_id ORDER BY loan_id) AS rn
            FROM loans
           WHERE status = 'ACTIVE' AND remaining_balance > 0 AND start_period <= p_period_code) l
      ON l.emp_id = t.emp_id AND l.rn = 1;

  INSERT INTO payslip_lines (payslip_id, comp_id, comp_code, comp_name_ar, comp_type, amount, is_taxable, calc_note, print_order)
  SELECT x.payslip_id, sc.comp_id, sc.code, sc.name_ar, 'DEDUCTION', x.inst, 0,
         CONCAT('Loan #', x.loan_id), sc.print_order
    FROM tmp_loan x JOIN salary_components sc ON sc.code = 'LOAN';

  INSERT INTO loan_installments (loan_id, payslip_id, period_code, amount)
  SELECT x.loan_id, x.payslip_id, p_period_code, x.inst FROM tmp_loan x;

  UPDATE loans l JOIN tmp_loan x ON x.loan_id = l.loan_id
     SET l.status = IF(l.remaining_balance - x.inst <= 0, 'CLOSED', 'ACTIVE'),
         l.remaining_balance = l.remaining_balance - x.inst;

  /* ---- 8. totals ------------------------------------------------------------ */
  UPDATE payslips p
    JOIN tmp_run t ON t.payslip_id = p.payslip_id
    JOIN (SELECT payslip_id,
                 IFNULL(SUM(IF(comp_type = 'EARNING', amount, 0)),0)   AS earnings,
                 IFNULL(SUM(IF(comp_type = 'DEDUCTION', amount, 0)),0) AS deductions
            FROM payslip_lines GROUP BY payslip_id) s ON s.payslip_id = p.payslip_id
     SET p.total_earnings     = s.earnings,
         p.total_deductions   = s.deductions,
         p.taxable_income     = t.taxable,
         p.income_tax         = t.tax,
         p.insurance_employee = t.ins_emp,
         p.insurance_employer = t.ins_er,
         p.net_pay            = s.earnings - s.deductions;

  DROP TEMPORARY TABLE IF EXISTS tmp_run, tmp_tax, tmp_loan;

  UPDATE payroll_periods
     SET status = 'PROCESSED', processed_at = NOW()
   WHERE period_id = v_period_id;
END$$
DELIMITER ;
