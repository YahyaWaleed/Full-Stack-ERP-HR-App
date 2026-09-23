-- V1's cursor-based sp_run_payroll, kept only so PayrollGoldenMasterTest can compare it with the V5 rewrite
CREATE PROCEDURE sp_run_payroll_legacy(IN p_period_code CHAR(7))
BEGIN
  DECLARE v_period_id  INT;
  DECLARE v_year       SMALLINT;
  DECLARE v_start      DATE;
  DECLARE v_end        DATE;
  DECLARE v_wdays      TINYINT;
  DECLARE v_dhours     DECIMAL(4,1);
  DECLARE v_ot_factor  DECIMAL(4,2);
  DECLARE v_r_emp      DECIMAL(5,2);
  DECLARE v_r_er       DECIMAL(5,2);

  DECLARE v_emp        INT;
  DECLARE v_hire       DATE;
  DECLARE v_basic      DECIMAL(12,2);
  DECLARE v_prorate    DECIMAL(6,4);
  DECLARE v_payslip    BIGINT;
  DECLARE v_gross      DECIMAL(12,2);
  DECLARE v_taxable    DECIMAL(12,2);
  DECLARE v_ins_wage   DECIMAL(12,2);
  DECLARE v_ins_emp    DECIMAL(12,2);
  DECLARE v_ins_er     DECIMAL(12,2);
  DECLARE v_tax        DECIMAL(12,2);
  DECLARE v_ot_hours   DECIMAL(6,2);
  DECLARE v_ot_amount  DECIMAL(12,2);
  DECLARE v_abs_days   DECIMAL(4,1);
  DECLARE v_abs_amount DECIMAL(12,2);
  DECLARE v_present    DECIMAL(4,1);
  DECLARE v_loan_id    INT;
  DECLARE v_loan_inst  DECIMAL(12,2);
  DECLARE v_done       INT DEFAULT 0;
  DECLARE v_status     VARCHAR(10);

  DECLARE cur_emp CURSOR FOR
    SELECT e.emp_id, e.hire_date
      FROM employees e
     WHERE (e.emp_status IN ('ACTIVE','PROBATION','SUSPENDED')
              OR e.termination_date IS NOT NULL)
       AND e.hire_date <= v_end
       AND (e.termination_date IS NULL OR e.termination_date >= v_start)
     ORDER BY e.emp_id;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_done = 1;

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

  SELECT daily_hours, overtime_factor, ins_employee_rate, ins_employer_rate
    INTO v_dhours, v_ot_factor, v_r_emp, v_r_er
    FROM payroll_settings WHERE fiscal_year = v_year;

  /* re-run safe: roll back loan installments, then wipe the old run */
  UPDATE loans l
    JOIN loan_installments li ON li.loan_id = l.loan_id AND li.period_code = p_period_code
     SET l.remaining_balance = l.remaining_balance + li.amount,
         l.status = IF(l.status = 'CANCELLED', 'CANCELLED', 'ACTIVE');  -- never revive a cancelled loan
  DELETE FROM loan_installments WHERE period_code = p_period_code;
  DELETE FROM payslips WHERE period_id = v_period_id;

  OPEN cur_emp;
  emp_loop: LOOP
    FETCH cur_emp INTO v_emp, v_hire;
    IF v_done = 1 THEN LEAVE emp_loop; END IF;

    SET v_basic = fn_current_basic(v_emp, v_end);
    IF v_basic <= 0 THEN ITERATE emp_loop; END IF;

    /* pro-rate the month for employees hired mid-period */
    SET v_prorate = IF(v_hire > v_start,
                       (DATEDIFF(v_end, v_hire) + 1) / (DATEDIFF(v_end, v_start) + 1), 1.0000);

    SELECT IFNULL(overtime_hours,0), IFNULL(unpaid_absent_days,0), IFNULL(present_days,0)
      INTO v_ot_hours, v_abs_days, v_present
      FROM attendance_summary WHERE period_id = v_period_id AND emp_id = v_emp;
    IF v_done = 1 THEN
      SET v_ot_hours = 0, v_abs_days = 0, v_present = v_wdays, v_done = 0;
    END IF;

    INSERT INTO payslips (payslip_no, period_id, emp_id, basic_salary,
                          worked_days, absent_days, overtime_hours, status)
    VALUES (CONCAT('PS-', REPLACE(p_period_code,'-',''), '-', LPAD(v_emp,4,'0')),
            v_period_id, v_emp, ROUND(v_basic * v_prorate, 2),
            v_present, v_abs_days, v_ot_hours, 'DRAFT');
    SET v_payslip = LAST_INSERT_ID();

    /* -------- 1. basic ------------------------------------------- */
    INSERT INTO payslip_lines (payslip_id, comp_id, comp_code, comp_name_ar, comp_type,
                               amount, is_taxable, calc_note, print_order)
    SELECT v_payslip, comp_id, code, name_ar, 'EARNING',
           ROUND(v_basic * v_prorate, 2), is_taxable,
           IF(v_prorate < 1, CONCAT('Pro-rated ', ROUND(v_prorate*100,1), '%'), 'Full month'), print_order
      FROM salary_components WHERE code = 'BASIC';

    /* -------- 2. recurring earnings ------------------------------ */
    INSERT INTO payslip_lines (payslip_id, comp_id, comp_code, comp_name_ar, comp_type,
                               amount, is_taxable, calc_note, print_order)
    SELECT v_payslip, sc.comp_id, sc.code, sc.name_ar, 'EARNING',
           ROUND(CASE sc.calc_type
                   WHEN 'PCT_BASIC' THEN v_basic * IFNULL(esc.percentage, sc.default_value) / 100
                   ELSE IFNULL(esc.amount, sc.default_value)
                 END * v_prorate, 2),
           sc.is_taxable,
           CASE sc.calc_type
             WHEN 'PCT_BASIC' THEN CONCAT(IFNULL(esc.percentage, sc.default_value), '% of basic')
             ELSE 'Fixed amount' END,
           sc.print_order
      FROM employee_salary_components esc
      JOIN salary_components sc ON sc.comp_id = esc.comp_id
     WHERE esc.emp_id = v_emp
       AND sc.comp_type = 'EARNING'
       AND sc.calc_type <> 'COMPUTED'
       AND sc.is_active = 1
       AND esc.effective_from <= v_end
       AND (esc.effective_to IS NULL OR esc.effective_to >= v_start);

    /* -------- 3. overtime ---------------------------------------- */
    IF v_ot_hours > 0 THEN
      SET v_ot_amount = ROUND((v_basic / v_wdays / v_dhours) * v_ot_factor * v_ot_hours, 2);
      INSERT INTO payslip_lines (payslip_id, comp_id, comp_code, comp_name_ar, comp_type,
                                 amount, is_taxable, calc_note, print_order)
      SELECT v_payslip, comp_id, code, name_ar, 'EARNING', v_ot_amount, is_taxable,
             CONCAT(v_ot_hours, ' hrs x ', v_ot_factor), print_order
        FROM salary_components WHERE code = 'OT';
    END IF;

    /* -------- 4. gross & taxable base ---------------------------- */
    SELECT IFNULL(SUM(amount),0), IFNULL(SUM(IF(is_taxable = 1, amount, 0)),0)
      INTO v_gross, v_taxable
      FROM payslip_lines WHERE payslip_id = v_payslip AND comp_type = 'EARNING';

    /* -------- 5. social insurance -------------------------------- */
    SET v_ins_wage = fn_insurance_wage(v_basic, v_year);
    SET v_ins_emp  = ROUND(v_ins_wage * v_r_emp / 100, 2);
    SET v_ins_er   = ROUND(v_ins_wage * v_r_er  / 100, 2);

    INSERT INTO payslip_lines (payslip_id, comp_id, comp_code, comp_name_ar, comp_type,
                               amount, is_taxable, calc_note, print_order)
    SELECT v_payslip, comp_id, code, name_ar, 'DEDUCTION', v_ins_emp, 0,
           CONCAT(v_r_emp, '% of ', v_ins_wage), print_order
      FROM salary_components WHERE code = 'INS';

    /* -------- 6. income tax -------------------------------------- */
    SET v_taxable = GREATEST(0, v_taxable - v_ins_emp);
    SET v_tax = fn_income_tax(v_taxable, v_year);

    IF v_tax > 0 THEN
      INSERT INTO payslip_lines (payslip_id, comp_id, comp_code, comp_name_ar, comp_type,
                                 amount, is_taxable, calc_note, print_order)
      SELECT v_payslip, comp_id, code, name_ar, 'DEDUCTION', v_tax, 0,
             CONCAT('Progressive on ', v_taxable), print_order
        FROM salary_components WHERE code = 'TAX';
    END IF;

    /* -------- 7a. unpaid absence --------------------------------- */
    IF v_abs_days > 0 THEN
      SET v_abs_amount = ROUND((v_gross / v_wdays) * v_abs_days, 2);
      INSERT INTO payslip_lines (payslip_id, comp_id, comp_code, comp_name_ar, comp_type,
                                 amount, is_taxable, calc_note, print_order)
      SELECT v_payslip, comp_id, code, name_ar, 'DEDUCTION', v_abs_amount, 0,
             CONCAT(v_abs_days, ' unpaid day(s)'), print_order
        FROM salary_components WHERE code = 'ABS';
    END IF;

    /* -------- 7b. other recurring deductions --------------------- */
    INSERT INTO payslip_lines (payslip_id, comp_id, comp_code, comp_name_ar, comp_type,
                               amount, is_taxable, calc_note, print_order)
    SELECT v_payslip, sc.comp_id, sc.code, sc.name_ar, 'DEDUCTION',
           ROUND(CASE sc.calc_type
                   WHEN 'PCT_BASIC' THEN v_basic * IFNULL(esc.percentage, sc.default_value) / 100
                   ELSE IFNULL(esc.amount, sc.default_value) END, 2),
           0,
           CASE sc.calc_type WHEN 'PCT_BASIC' THEN 'Percentage of basic' ELSE 'Fixed amount' END,
           sc.print_order
      FROM employee_salary_components esc
      JOIN salary_components sc ON sc.comp_id = esc.comp_id
     WHERE esc.emp_id = v_emp
       AND sc.comp_type = 'DEDUCTION'
       AND sc.calc_type <> 'COMPUTED'
       AND sc.is_active = 1
       AND esc.effective_from <= v_end
       AND (esc.effective_to IS NULL OR esc.effective_to >= v_start);

    /* -------- 7c. loan installment ------------------------------- */
    SET v_loan_id = NULL;
    SELECT loan_id, LEAST(monthly_installment, remaining_balance)
      INTO v_loan_id, v_loan_inst
      FROM loans
     WHERE emp_id = v_emp AND status = 'ACTIVE'
       AND remaining_balance > 0 AND start_period <= p_period_code
     ORDER BY loan_id LIMIT 1;
    IF v_done = 1 THEN SET v_done = 0; SET v_loan_id = NULL; END IF;

    IF v_loan_id IS NOT NULL THEN
      INSERT INTO payslip_lines (payslip_id, comp_id, comp_code, comp_name_ar, comp_type,
                                 amount, is_taxable, calc_note, print_order)
      SELECT v_payslip, comp_id, code, name_ar, 'DEDUCTION', v_loan_inst, 0,
             CONCAT('Loan #', v_loan_id), print_order
        FROM salary_components WHERE code = 'LOAN';

      INSERT INTO loan_installments (loan_id, payslip_id, period_code, amount)
      VALUES (v_loan_id, v_payslip, p_period_code, v_loan_inst);

      UPDATE loans
         SET status = IF(remaining_balance - v_loan_inst <= 0, 'CLOSED', 'ACTIVE'),
             remaining_balance = remaining_balance - v_loan_inst
       WHERE loan_id = v_loan_id;
    END IF;

    /* -------- 8. totals ------------------------------------------ */
    UPDATE payslips p
       SET p.total_earnings   = (SELECT IFNULL(SUM(amount),0) FROM payslip_lines
                                  WHERE payslip_id = v_payslip AND comp_type = 'EARNING'),
           p.total_deductions = (SELECT IFNULL(SUM(amount),0) FROM payslip_lines
                                  WHERE payslip_id = v_payslip AND comp_type = 'DEDUCTION'),
           p.taxable_income   = v_taxable,
           p.income_tax       = v_tax,
           p.insurance_employee = v_ins_emp,
           p.insurance_employer = v_ins_er
     WHERE p.payslip_id = v_payslip;

    UPDATE payslips SET net_pay = total_earnings - total_deductions
     WHERE payslip_id = v_payslip;

  END LOOP;
  CLOSE cur_emp;

  UPDATE payroll_periods
     SET status = 'PROCESSED', processed_at = NOW()
   WHERE period_id = v_period_id;
END
