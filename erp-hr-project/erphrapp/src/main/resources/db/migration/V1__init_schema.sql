/* =====================================================================
   V1  --  ERP HR & Payroll schema: tables, payroll engine, reporting views
   Engine: MySQL 8.0+ | Charset: utf8mb4
   Applied by Flyway on startup. Never edit an applied migration --
   add a new V<n>__<description>.sql instead.
   ===================================================================== */


SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 1;

/* =====================================================================
   01. ORGANISATION STRUCTURE
   ===================================================================== */

CREATE TABLE branches (
  branch_id     INT AUTO_INCREMENT PRIMARY KEY,
  code          VARCHAR(10)  NOT NULL UNIQUE,
  name_en       VARCHAR(80)  NOT NULL,
  name_ar       VARCHAR(80)  NOT NULL,
  city          VARCHAR(50)  NOT NULL,
  country       VARCHAR(50)  NOT NULL DEFAULT 'Egypt',
  address       VARCHAR(200),
  is_active     TINYINT(1)   NOT NULL DEFAULT 1
) ENGINE=InnoDB;

CREATE TABLE departments (
  dept_id        INT AUTO_INCREMENT PRIMARY KEY,
  code           VARCHAR(10) NOT NULL UNIQUE,
  name_en        VARCHAR(80) NOT NULL,
  name_ar        VARCHAR(80) NOT NULL,
  parent_dept_id INT NULL,
  cost_center    VARCHAR(20) NOT NULL,
  branch_id      INT NOT NULL,
  is_active      TINYINT(1)  NOT NULL DEFAULT 1,
  CONSTRAINT fk_dept_parent FOREIGN KEY (parent_dept_id) REFERENCES departments(dept_id),
  CONSTRAINT fk_dept_branch FOREIGN KEY (branch_id)      REFERENCES branches(branch_id)
) ENGINE=InnoDB;

CREATE TABLE job_titles (
  job_id      INT AUTO_INCREMENT PRIMARY KEY,
  code        VARCHAR(12) NOT NULL UNIQUE,
  title_en    VARCHAR(80) NOT NULL,
  title_ar    VARCHAR(80) NOT NULL,
  job_grade   ENUM('G1','G2','G3','G4','G5','G6','G7') NOT NULL,
  min_salary  DECIMAL(12,2) NOT NULL,
  max_salary  DECIMAL(12,2) NOT NULL,
  is_managerial TINYINT(1) NOT NULL DEFAULT 0,
  CONSTRAINT chk_job_range CHECK (max_salary >= min_salary)
) ENGINE=InnoDB;


/* =====================================================================
   02. EMPLOYEES
   ===================================================================== */

CREATE TABLE hr_users (
    user_id   INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(50) NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,
    role      ENUM('HR_ADMIN','HR_USER') NOT NULL
) ENGINE=InnoDB;

CREATE TABLE employees (
  emp_id          INT AUTO_INCREMENT PRIMARY KEY,
  emp_code        VARCHAR(12) NOT NULL UNIQUE,
  full_name_ar    VARCHAR(120) NOT NULL,
  full_name_en    VARCHAR(120) NOT NULL,
  gender          ENUM('M','F') NOT NULL,
  birth_date      DATE NOT NULL,
  national_id     CHAR(14) NOT NULL UNIQUE,
  marital_status  ENUM('SINGLE','MARRIED','DIVORCED','WIDOWED') NOT NULL DEFAULT 'SINGLE',
  dependents      TINYINT NOT NULL DEFAULT 0,
  email           VARCHAR(100) UNIQUE,
  mobile          VARCHAR(20),
  address         VARCHAR(200),
  hire_date       DATE NOT NULL,
  dept_id         INT NOT NULL,
  job_id          INT NOT NULL,
  branch_id       INT NOT NULL,
  manager_id      INT NULL,
  emp_status      ENUM('ACTIVE','PROBATION','SUSPENDED','RESIGNED','TERMINATED') NOT NULL DEFAULT 'ACTIVE',
  termination_date DATE NULL,
  insurance_no    VARCHAR(20) UNIQUE,
  bank_name       VARCHAR(60),
  bank_account    VARCHAR(34),
  payment_method  ENUM('BANK','CASH','CHEQUE') NOT NULL DEFAULT 'BANK',
  created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_emp_dept    FOREIGN KEY (dept_id)    REFERENCES departments(dept_id),
  CONSTRAINT fk_emp_job     FOREIGN KEY (job_id)     REFERENCES job_titles(job_id),
  CONSTRAINT fk_emp_branch  FOREIGN KEY (branch_id)  REFERENCES branches(branch_id),
  CONSTRAINT fk_emp_manager FOREIGN KEY (manager_id) REFERENCES employees(emp_id),
  CONSTRAINT chk_term CHECK (termination_date IS NULL OR termination_date >= hire_date),
  INDEX idx_emp_dept (dept_id),
  INDEX idx_emp_status (emp_status),
  INDEX idx_emp_hire (hire_date)
) ENGINE=InnoDB;

CREATE TABLE employee_contracts (
  contract_id    INT AUTO_INCREMENT PRIMARY KEY,
  emp_id         INT NOT NULL,
  contract_no    VARCHAR(20) NOT NULL UNIQUE,
  contract_type  ENUM('PERMANENT','FIXED_TERM','PART_TIME','CONSULTANT','INTERN') NOT NULL,
  start_date     DATE NOT NULL,
  end_date       DATE NULL,
  basic_salary   DECIMAL(12,2) NOT NULL,
  currency       CHAR(3) NOT NULL DEFAULT 'EGP',
  weekly_hours   DECIMAL(4,1) NOT NULL DEFAULT 40.0,
  annual_leave_days SMALLINT NOT NULL DEFAULT 21,
  probation_months TINYINT NOT NULL DEFAULT 3,
  status         ENUM('ACTIVE','EXPIRED','TERMINATED') NOT NULL DEFAULT 'ACTIVE',
  notes          VARCHAR(255),
  CONSTRAINT fk_contract_emp FOREIGN KEY (emp_id) REFERENCES employees(emp_id) ON DELETE CASCADE,
  CONSTRAINT chk_contract_dates CHECK (end_date IS NULL OR end_date > start_date),
  CONSTRAINT chk_basic_positive CHECK (basic_salary > 0),
  INDEX idx_contract_emp (emp_id, status)
) ENGINE=InnoDB;

/* =====================================================================
   03. SALARY STRUCTURE
   ===================================================================== */

CREATE TABLE payroll_settings (
  fiscal_year         SMALLINT PRIMARY KEY,
  personal_exemption  DECIMAL(12,2) NOT NULL,   -- annual, income-tax exemption
  ins_min_wage        DECIMAL(12,2) NOT NULL,   -- social insurance floor  (monthly)
  ins_max_wage        DECIMAL(12,2) NOT NULL,   -- social insurance ceiling(monthly)
  ins_employee_rate   DECIMAL(5,2)  NOT NULL,   -- %
  ins_employer_rate   DECIMAL(5,2)  NOT NULL,   -- %
  working_days_month  TINYINT       NOT NULL DEFAULT 30,
  daily_hours         DECIMAL(4,1)  NOT NULL DEFAULT 8.0,
  overtime_factor     DECIMAL(4,2)  NOT NULL DEFAULT 1.50
) ENGINE=InnoDB;

CREATE TABLE tax_brackets (
  bracket_id  INT AUTO_INCREMENT PRIMARY KEY,
  fiscal_year SMALLINT NOT NULL,
  from_amount DECIMAL(14,2) NOT NULL,           -- annual taxable income
  to_amount   DECIMAL(14,2) NOT NULL,
  rate        DECIMAL(5,2)  NOT NULL,           -- %
  CONSTRAINT fk_bracket_year FOREIGN KEY (fiscal_year) REFERENCES payroll_settings(fiscal_year),
  UNIQUE KEY uq_bracket (fiscal_year, from_amount)
) ENGINE=InnoDB;

CREATE TABLE salary_components (
  comp_id        INT AUTO_INCREMENT PRIMARY KEY,
  code           VARCHAR(12) NOT NULL UNIQUE,
  name_en        VARCHAR(60) NOT NULL,
  name_ar        VARCHAR(60) NOT NULL,
  comp_type      ENUM('EARNING','DEDUCTION') NOT NULL,
  calc_type      ENUM('FIXED','PCT_BASIC','COMPUTED') NOT NULL,
  default_value  DECIMAL(12,2) NOT NULL DEFAULT 0,   -- amount, or % when PCT_BASIC
  is_taxable     TINYINT(1) NOT NULL DEFAULT 1,
  is_insurable   TINYINT(1) NOT NULL DEFAULT 0,
  is_recurring   TINYINT(1) NOT NULL DEFAULT 1,
  print_order    SMALLINT NOT NULL DEFAULT 100,
  is_active      TINYINT(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB;

CREATE TABLE employee_salary_components (
  id             INT AUTO_INCREMENT PRIMARY KEY,
  emp_id         INT NOT NULL,
  comp_id        INT NOT NULL,
  amount         DECIMAL(12,2) NULL,   -- overrides default (FIXED)
  percentage     DECIMAL(5,2)  NULL,   -- overrides default (PCT_BASIC)
  effective_from DATE NOT NULL,
  effective_to   DATE NULL,
  notes          VARCHAR(150),
  CONSTRAINT fk_esc_emp  FOREIGN KEY (emp_id)  REFERENCES employees(emp_id) ON DELETE CASCADE,
  CONSTRAINT fk_esc_comp FOREIGN KEY (comp_id) REFERENCES salary_components(comp_id),
  UNIQUE KEY uq_esc (emp_id, comp_id, effective_from),
  INDEX idx_esc_lookup (emp_id, effective_from, effective_to)
) ENGINE=InnoDB;

/* =====================================================================
   04. LEAVES & ATTENDANCE
   ===================================================================== */

CREATE TABLE leave_types (
  type_id        INT AUTO_INCREMENT PRIMARY KEY,
  code           VARCHAR(10) NOT NULL UNIQUE,
  name_en        VARCHAR(60) NOT NULL,
  name_ar        VARCHAR(60) NOT NULL,
  annual_quota   SMALLINT NOT NULL DEFAULT 0,
  is_paid        TINYINT(1) NOT NULL DEFAULT 1,
  affects_balance TINYINT(1) NOT NULL DEFAULT 1,
  max_consecutive SMALLINT NOT NULL DEFAULT 30,
  requires_attachment TINYINT(1) NOT NULL DEFAULT 0,
  gender_restriction ENUM('ANY','M','F') NOT NULL DEFAULT 'ANY'
) ENGINE=InnoDB;

CREATE TABLE leave_balances (
  balance_id      INT AUTO_INCREMENT PRIMARY KEY,
  emp_id          INT NOT NULL,
  type_id         INT NOT NULL,
  fiscal_year     SMALLINT NOT NULL,
  entitled_days   DECIMAL(5,1) NOT NULL DEFAULT 0,
  carried_forward DECIMAL(5,1) NOT NULL DEFAULT 0,
  used_days       DECIMAL(5,1) NOT NULL DEFAULT 0,
  remaining_days  DECIMAL(5,1) AS (entitled_days + carried_forward - used_days) STORED,
  CONSTRAINT fk_bal_emp  FOREIGN KEY (emp_id)  REFERENCES employees(emp_id) ON DELETE CASCADE,
  CONSTRAINT fk_bal_type FOREIGN KEY (type_id) REFERENCES leave_types(type_id),
  UNIQUE KEY uq_balance (emp_id, type_id, fiscal_year)
) ENGINE=InnoDB;

CREATE TABLE leave_requests (
  request_id   INT AUTO_INCREMENT PRIMARY KEY,
  emp_id       INT NOT NULL,
  type_id      INT NOT NULL,
  start_date   DATE NOT NULL,
  end_date     DATE NOT NULL,
  days_count   DECIMAL(4,1) NOT NULL,
  reason       VARCHAR(200),
  status       ENUM('PENDING','APPROVED','REJECTED','CANCELLED') NOT NULL DEFAULT 'PENDING',
  applied_on   DATE NOT NULL,
  approver_id  INT NULL,
  decided_on   DATE NULL,
  reject_reason VARCHAR(200),
  CONSTRAINT fk_lr_emp      FOREIGN KEY (emp_id)      REFERENCES employees(emp_id) ON DELETE CASCADE,
  CONSTRAINT fk_lr_type     FOREIGN KEY (type_id)     REFERENCES leave_types(type_id),
  CONSTRAINT fk_lr_approver FOREIGN KEY (approver_id) REFERENCES employees(emp_id),
  CONSTRAINT chk_lr_dates CHECK (end_date >= start_date),
  INDEX idx_lr_emp (emp_id, status),
  INDEX idx_lr_period (start_date, end_date)
) ENGINE=InnoDB;

/* =====================================================================
   05. LOANS / SALARY ADVANCES
   ===================================================================== */

CREATE TABLE loans (
  loan_id            INT AUTO_INCREMENT PRIMARY KEY,
  emp_id             INT NOT NULL,
  loan_type          ENUM('ADVANCE','PERSONAL','EMERGENCY','HOUSING') NOT NULL,
  principal_amount   DECIMAL(12,2) NOT NULL,
  installments_count SMALLINT NOT NULL,
  monthly_installment DECIMAL(12,2) NOT NULL,
  remaining_balance  DECIMAL(12,2) NOT NULL,
  start_period       CHAR(7) NOT NULL,          -- YYYY-MM
  approved_by        INT NULL,
  status             ENUM('ACTIVE','CLOSED','CANCELLED') NOT NULL DEFAULT 'ACTIVE',
  request_date       DATE NOT NULL,
  CONSTRAINT fk_loan_emp FOREIGN KEY (emp_id) REFERENCES employees(emp_id) ON DELETE CASCADE,
  CONSTRAINT fk_loan_appr FOREIGN KEY (approved_by) REFERENCES employees(emp_id),
  CONSTRAINT chk_loan_amt CHECK (principal_amount > 0 AND monthly_installment > 0),
  INDEX idx_loan_emp (emp_id, status)
) ENGINE=InnoDB;

/* =====================================================================
   06. PAYROLL PERIODS, PAYSLIPS & PAYMENTS
   ===================================================================== */

CREATE TABLE payroll_periods (
  period_id    INT AUTO_INCREMENT PRIMARY KEY,
  period_code  CHAR(7) NOT NULL UNIQUE,          -- 2026-07
  fiscal_year  SMALLINT NOT NULL,
  start_date   DATE NOT NULL,
  end_date     DATE NOT NULL,
  pay_date     DATE NOT NULL,
  working_days TINYINT NOT NULL DEFAULT 22,
  status       ENUM('OPEN','PROCESSED','APPROVED','PAID','CLOSED') NOT NULL DEFAULT 'OPEN',
  processed_at DATETIME NULL,
  approved_by  INT NULL,
  CONSTRAINT chk_period_dates CHECK (end_date > start_date)
) ENGINE=InnoDB;

CREATE TABLE attendance_summary (
  att_id            INT AUTO_INCREMENT PRIMARY KEY,
  period_id         INT NOT NULL,
  emp_id            INT NOT NULL,
  working_days      DECIMAL(4,1) NOT NULL,
  present_days      DECIMAL(4,1) NOT NULL,
  paid_leave_days   DECIMAL(4,1) NOT NULL DEFAULT 0,
  unpaid_absent_days DECIMAL(4,1) NOT NULL DEFAULT 0,
  overtime_hours    DECIMAL(6,2) NOT NULL DEFAULT 0,
  late_minutes      SMALLINT NOT NULL DEFAULT 0,
  CONSTRAINT fk_att_period FOREIGN KEY (period_id) REFERENCES payroll_periods(period_id) ON DELETE CASCADE,
  CONSTRAINT fk_att_emp    FOREIGN KEY (emp_id)    REFERENCES employees(emp_id) ON DELETE CASCADE,
  UNIQUE KEY uq_att (period_id, emp_id)
) ENGINE=InnoDB;

CREATE TABLE payslips (
  payslip_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  payslip_no        VARCHAR(24) NOT NULL UNIQUE,
  period_id         INT NOT NULL,
  emp_id            INT NOT NULL,
  basic_salary      DECIMAL(12,2) NOT NULL DEFAULT 0,
  total_earnings    DECIMAL(12,2) NOT NULL DEFAULT 0,   -- gross
  total_deductions  DECIMAL(12,2) NOT NULL DEFAULT 0,
  taxable_income    DECIMAL(12,2) NOT NULL DEFAULT 0,
  income_tax        DECIMAL(12,2) NOT NULL DEFAULT 0,
  insurance_employee DECIMAL(12,2) NOT NULL DEFAULT 0,
  insurance_employer DECIMAL(12,2) NOT NULL DEFAULT 0,
  net_pay           DECIMAL(12,2) NOT NULL DEFAULT 0,
  worked_days       DECIMAL(4,1) NOT NULL DEFAULT 0,
  absent_days       DECIMAL(4,1) NOT NULL DEFAULT 0,
  overtime_hours    DECIMAL(6,2) NOT NULL DEFAULT 0,
  currency          CHAR(3) NOT NULL DEFAULT 'EGP',
  status            ENUM('DRAFT','APPROVED','PAID','CANCELLED') NOT NULL DEFAULT 'DRAFT',
  generated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_ps_period FOREIGN KEY (period_id) REFERENCES payroll_periods(period_id) ON DELETE CASCADE,
  CONSTRAINT fk_ps_emp    FOREIGN KEY (emp_id)    REFERENCES employees(emp_id),
  UNIQUE KEY uq_payslip (period_id, emp_id),
  INDEX idx_ps_emp (emp_id)
) ENGINE=InnoDB;

CREATE TABLE payslip_lines (
  line_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
  payslip_id  BIGINT NOT NULL,
  comp_id     INT NOT NULL,
  comp_code   VARCHAR(12) NOT NULL,
  comp_name_ar VARCHAR(60) NOT NULL,
  comp_type   ENUM('EARNING','DEDUCTION') NOT NULL,
  amount      DECIMAL(12,2) NOT NULL,
  is_taxable  TINYINT(1) NOT NULL DEFAULT 1,
  calc_note   VARCHAR(120),
  print_order SMALLINT NOT NULL DEFAULT 100,
  CONSTRAINT fk_pl_payslip FOREIGN KEY (payslip_id) REFERENCES payslips(payslip_id) ON DELETE CASCADE,
  CONSTRAINT fk_pl_comp    FOREIGN KEY (comp_id)    REFERENCES salary_components(comp_id),
  INDEX idx_pl_payslip (payslip_id, comp_type)
) ENGINE=InnoDB;

CREATE TABLE loan_installments (
  inst_id    INT AUTO_INCREMENT PRIMARY KEY,
  loan_id    INT NOT NULL,
  payslip_id BIGINT NULL,
  period_code CHAR(7) NOT NULL,
  amount     DECIMAL(12,2) NOT NULL,
  paid_on    DATE NULL,
  CONSTRAINT fk_inst_loan    FOREIGN KEY (loan_id)    REFERENCES loans(loan_id) ON DELETE CASCADE,
  CONSTRAINT fk_inst_payslip FOREIGN KEY (payslip_id) REFERENCES payslips(payslip_id) ON DELETE SET NULL,
  UNIQUE KEY uq_inst (loan_id, period_code)
) ENGINE=InnoDB;

CREATE TABLE payroll_payments (
  payment_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
  payslip_id  BIGINT NOT NULL,
  method      ENUM('BANK','CASH','CHEQUE') NOT NULL,
  bank_name   VARCHAR(60),
  bank_account VARCHAR(34),
  amount      DECIMAL(12,2) NOT NULL,
  paid_on     DATE NOT NULL,
  reference   VARCHAR(40),
  CONSTRAINT fk_pay_payslip FOREIGN KEY (payslip_id) REFERENCES payslips(payslip_id) ON DELETE CASCADE,
  UNIQUE KEY uq_payment (payslip_id)
) ENGINE=InnoDB;

/* =====================================================================
   07. PAYROLL ENGINE  (functions, procedures, triggers)
   ===================================================================== */

DELIMITER $$

/* ---- Progressive income tax on annualised taxable income ---------- */
CREATE FUNCTION fn_income_tax(p_monthly_taxable DECIMAL(12,2), p_year SMALLINT)
RETURNS DECIMAL(12,2)
READS SQL DATA
BEGIN
  DECLARE v_annual   DECIMAL(14,2);
  DECLARE v_exempt   DECIMAL(14,2);
  DECLARE v_tax      DECIMAL(14,2);

  SELECT personal_exemption INTO v_exempt FROM payroll_settings WHERE fiscal_year = p_year;
  SET v_annual = GREATEST(0, (p_monthly_taxable * 12) - IFNULL(v_exempt, 0));

  SELECT IFNULL(SUM(GREATEST(0, LEAST(v_annual, to_amount) - from_amount) * rate / 100), 0)
    INTO v_tax
  FROM tax_brackets
  WHERE fiscal_year = p_year;

  RETURN ROUND(v_tax / 12, 2);
END$$

/* ---- Social-insurance wage (floor / ceiling applied) --------------- */
CREATE FUNCTION fn_insurance_wage(p_basic DECIMAL(12,2), p_year SMALLINT)
RETURNS DECIMAL(12,2)
READS SQL DATA
BEGIN
  DECLARE v_min DECIMAL(12,2);
  DECLARE v_max DECIMAL(12,2);
  SELECT ins_min_wage, ins_max_wage INTO v_min, v_max
    FROM payroll_settings WHERE fiscal_year = p_year;
  RETURN LEAST(GREATEST(p_basic, v_min), v_max);
END$$

/* ---- Current basic salary of an employee at a given date ---------- */
CREATE FUNCTION fn_current_basic(p_emp INT, p_date DATE)
RETURNS DECIMAL(12,2)
READS SQL DATA
BEGIN
  DECLARE v_basic DECIMAL(12,2);
  SELECT basic_salary INTO v_basic
    FROM employee_contracts
   WHERE emp_id = p_emp
     AND start_date <= p_date
     AND (end_date IS NULL OR end_date >= p_date)
     AND status = 'ACTIVE'
   ORDER BY start_date DESC LIMIT 1;
  RETURN IFNULL(v_basic, 0);
END$$

/* =====================================================================
   sp_run_payroll : generates payslips + payslip lines for one period
   --------------------------------------------------------------------
   Order of calculation
     1. basic salary (pro-rated for mid-month hires)
     2. recurring earnings  (fixed / % of basic)
     3. overtime            (basic / days / hours * factor)
     4. gross               = SUM(earnings)
     5. social insurance    (employee 11% / employer 18.75% of ins. wage)
     6. income tax          (progressive, on taxable earnings - insurance)
     7. absence deduction, loan installment, recurring deductions
     8. net pay             = gross - SUM(deductions)
   ===================================================================== */
CREATE PROCEDURE sp_run_payroll(IN p_period_code CHAR(7))
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
END$$

/* ---- Approve + pay a whole period -------------------------------- */
CREATE PROCEDURE sp_pay_period(IN p_period_code CHAR(7), IN p_reference_prefix VARCHAR(10))
BEGIN
  DECLARE v_period_id INT;
  DECLARE v_pay_date  DATE;

  SELECT period_id, pay_date INTO v_period_id, v_pay_date
    FROM payroll_periods WHERE period_code = p_period_code;

  UPDATE payslips SET status = 'PAID' WHERE period_id = v_period_id AND status <> 'CANCELLED';

  INSERT INTO payroll_payments (payslip_id, method, bank_name, bank_account, amount, paid_on, reference)
  SELECT p.payslip_id, e.payment_method, e.bank_name, e.bank_account, p.net_pay, v_pay_date,
         CONCAT(p_reference_prefix, '-', REPLACE(p_period_code,'-',''), '-', LPAD(e.emp_id,4,'0'))
    FROM payslips p JOIN employees e ON e.emp_id = p.emp_id
   WHERE p.period_id = v_period_id AND p.status = 'PAID';

  UPDATE loan_installments li
    JOIN payslips p ON p.payslip_id = li.payslip_id
     SET li.paid_on = v_pay_date
   WHERE p.period_id = v_period_id;

  UPDATE payroll_periods SET status = 'PAID' WHERE period_id = v_period_id;
END$$

/* ---- Keep leave balances in sync with approvals ------------------- */
CREATE TRIGGER trg_leave_approved
AFTER UPDATE ON leave_requests
FOR EACH ROW
BEGIN
  IF NEW.status = 'APPROVED' AND OLD.status <> 'APPROVED' THEN
    UPDATE leave_balances
       SET used_days = used_days + NEW.days_count
     WHERE emp_id = NEW.emp_id AND type_id = NEW.type_id
       AND fiscal_year = YEAR(NEW.start_date);
  ELSEIF OLD.status = 'APPROVED' AND NEW.status IN ('CANCELLED','REJECTED') THEN
    UPDATE leave_balances
       SET used_days = GREATEST(0, used_days - NEW.days_count)
     WHERE emp_id = NEW.emp_id AND type_id = NEW.type_id
       AND fiscal_year = YEAR(NEW.start_date);
  END IF;
END$$

/* ---- Auto-create leave balances for a new employee ---------------- */



DELIMITER ;

/* =====================================================================
   08. REPORTING VIEWS
   ===================================================================== */

CREATE OR REPLACE VIEW v_employee_directory AS
SELECT e.emp_id, e.emp_code, e.full_name_ar, e.full_name_en, e.gender,
       TIMESTAMPDIFF(YEAR, e.birth_date, CURDATE())        AS age,
       d.name_ar   AS department, j.title_ar AS job_title, j.job_grade,
       b.name_ar   AS branch,     m.full_name_ar AS manager,
       e.hire_date,
       TIMESTAMPDIFF(YEAR, e.hire_date, CURDATE())         AS years_of_service,
       e.emp_status, c.contract_type, c.basic_salary, e.email, e.mobile
FROM employees e
JOIN departments d ON d.dept_id = e.dept_id
JOIN job_titles  j ON j.job_id  = e.job_id
JOIN branches    b ON b.branch_id = e.branch_id
LEFT JOIN employees m ON m.emp_id = e.manager_id
LEFT JOIN employee_contracts c ON c.emp_id = e.emp_id AND c.status = 'ACTIVE';

CREATE OR REPLACE VIEW v_payslip_header AS
SELECT p.payslip_id, p.payslip_no, pp.period_code, pp.pay_date,
       e.emp_code, e.full_name_ar, d.name_ar AS department, j.title_ar AS job_title,
       p.basic_salary, p.total_earnings AS gross_pay, p.total_deductions,
       p.insurance_employee, p.income_tax, p.net_pay,
       p.worked_days, p.absent_days, p.overtime_hours,
       p.insurance_employer,
       (p.total_earnings + p.insurance_employer) AS total_company_cost,
       p.status, p.currency
FROM payslips p
JOIN payroll_periods pp ON pp.period_id = p.period_id
JOIN employees e  ON e.emp_id  = p.emp_id
JOIN departments d ON d.dept_id = e.dept_id
JOIN job_titles  j ON j.job_id  = e.job_id;

CREATE OR REPLACE VIEW v_payslip_detail AS
SELECT pp.period_code, e.emp_code, e.full_name_ar, p.payslip_no,
       l.comp_type, l.comp_code, l.comp_name_ar, l.amount, l.calc_note
FROM payslip_lines l
JOIN payslips p        ON p.payslip_id = l.payslip_id
JOIN payroll_periods pp ON pp.period_id = p.period_id
JOIN employees e       ON e.emp_id = p.emp_id
ORDER BY pp.period_code, e.emp_code, l.comp_type DESC, l.print_order;

CREATE OR REPLACE VIEW v_payroll_cost_by_dept AS
SELECT pp.period_code, d.name_ar AS department, COUNT(*) AS employees,
       ROUND(SUM(p.basic_salary),2)       AS total_basic,
       ROUND(SUM(p.total_earnings),2)     AS total_gross,
       ROUND(SUM(p.income_tax),2)         AS total_tax,
       ROUND(SUM(p.insurance_employee),2) AS insurance_employee,
       ROUND(SUM(p.insurance_employer),2) AS insurance_employer,
       ROUND(SUM(p.net_pay),2)            AS total_net,
       ROUND(SUM(p.total_earnings + p.insurance_employer),2) AS company_cost
FROM payslips p
JOIN payroll_periods pp ON pp.period_id = p.period_id
JOIN employees e  ON e.emp_id = p.emp_id
JOIN departments d ON d.dept_id = e.dept_id
GROUP BY pp.period_code, d.name_ar;

CREATE OR REPLACE VIEW v_leave_balances AS
SELECT e.emp_code, e.full_name_ar, d.name_ar AS department,
       lt.name_ar AS leave_type, lb.fiscal_year,
       lb.entitled_days, lb.carried_forward, lb.used_days, lb.remaining_days
FROM leave_balances lb
JOIN employees e   ON e.emp_id = lb.emp_id
JOIN leave_types lt ON lt.type_id = lb.type_id
JOIN departments d  ON d.dept_id = e.dept_id;

CREATE OR REPLACE VIEW v_leave_requests_log AS
SELECT lr.request_id, e.emp_code, e.full_name_ar, lt.name_ar AS leave_type,
       lr.start_date, lr.end_date, lr.days_count, lr.status,
       a.full_name_ar AS approved_by, lr.applied_on, lr.decided_on, lr.reason
FROM leave_requests lr
JOIN employees e    ON e.emp_id = lr.emp_id
JOIN leave_types lt ON lt.type_id = lr.type_id
LEFT JOIN employees a ON a.emp_id = lr.approver_id;

CREATE OR REPLACE VIEW v_headcount_by_dept AS
SELECT d.code, d.name_ar AS department, b.name_ar AS branch,
       COUNT(e.emp_id) AS headcount,
       SUM(e.gender = 'M') AS males, SUM(e.gender = 'F') AS females,
       ROUND(AVG(TIMESTAMPDIFF(YEAR, e.hire_date, CURDATE())),1) AS avg_service_years,
       ROUND(AVG(fn_current_basic(e.emp_id, CURDATE())),2) AS avg_basic_salary
FROM departments d
JOIN branches b ON b.branch_id = d.branch_id
LEFT JOIN employees e ON e.dept_id = d.dept_id AND e.emp_status IN ('ACTIVE','PROBATION')
GROUP BY d.code, d.name_ar, b.name_ar;

CREATE OR REPLACE VIEW v_active_loans AS
SELECT l.loan_id, e.emp_code, e.full_name_ar, l.loan_type, l.principal_amount,
       l.installments_count, l.monthly_installment, l.remaining_balance,
       ROUND(100 * (l.principal_amount - l.remaining_balance) / l.principal_amount, 1) AS paid_pct,
       l.start_period, l.status
FROM loans l JOIN employees e ON e.emp_id = l.emp_id;
