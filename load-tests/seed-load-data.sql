/* =====================================================================
   Load-test data: +5,000 employees and 12 months of payroll history.
   N+1 queries and slow reports only show up at volume, so run the load test
   against a database seeded with this -- never against production.

     mysql -h 127.0.0.1 -P 3306 -u root -p erp_hr < load-tests/seed-load-data.sql

   Expects a dev database (schema + demo data, i.e. the app started once with the
   dev profile). Takes a minute or two; safe to run once per fresh database.
   ===================================================================== */

SET SESSION cte_max_recursion_depth = 10000;
SET @n := 5000;

-- ---- employees (codes come from the trg_employees_code trigger) -------------
INSERT INTO employees (full_name_ar, full_name_en, gender, birth_date, national_id, marital_status, dependents,
                       hire_date, dept_id, job_id, branch_id, emp_status, payment_method, bank_name, bank_account)
WITH RECURSIVE seq(i) AS (SELECT 1 UNION ALL SELECT i + 1 FROM seq WHERE i < @n)
SELECT CONCAT('موظف اختبار ', i),
       CONCAT('Load Employee ', i),
       IF(i % 2 = 0, 'F', 'M'),
       DATE_ADD('1970-01-01', INTERVAL (i * 7) % 12000 DAY),
       CONCAT('39', LPAD(i, 12, '0')),
       IF(i % 3 = 0, 'MARRIED', 'SINGLE'),
       i % 4,
       DATE_ADD('2015-01-01', INTERVAL i % 3900 DAY),
       1 + (i % 10),
       (SELECT MIN(job_id) FROM job_titles) + (i % 5),
       1 + (i % 3),
       'ACTIVE',
       IF(i % 10 = 0, 'CASH', 'BANK'),
       'CIB',
       CONCAT('EG38LOAD', LPAD(i, 12, '0'))
  FROM seq;

-- ---- one active contract each -------------------------------------------------
INSERT INTO employee_contracts (emp_id, contract_no, contract_type, start_date, basic_salary)
SELECT emp_id, CONCAT('CT-LOAD-', LPAD(emp_id, 6, '0')), 'PERMANENT', hire_date, 5000 + (emp_id % 60) * 500
  FROM employees WHERE full_name_en LIKE 'Load Employee %';

-- ---- a couple of recurring allowances -------------------------------------------
INSERT INTO employee_salary_components (emp_id, comp_id, amount, effective_from)
SELECT e.emp_id, sc.comp_id, NULL, e.hire_date
  FROM employees e JOIN salary_components sc ON sc.code IN ('TRAN', 'FOOD')
 WHERE e.full_name_en LIKE 'Load Employee %';

-- ---- leave balances for 2026 ------------------------------------------------------
INSERT INTO leave_balances (emp_id, type_id, fiscal_year, entitled_days, carried_forward, used_days)
SELECT e.emp_id, t.type_id, 2026, t.annual_quota, 0, 0
  FROM employees e JOIN leave_types t ON t.affects_balance = 1 AND t.gender_restriction IN ('ANY', e.gender)
 WHERE e.full_name_en LIKE 'Load Employee %';

-- ---- 8 more months so there are 12 in total ------------------------------------------
INSERT INTO payroll_periods (period_code, fiscal_year, start_date, end_date, pay_date, working_days, status)
WITH RECURSIVE m(k) AS (SELECT 1 UNION ALL SELECT k + 1 FROM m WHERE k < 8)
SELECT DATE_FORMAT(DATE_SUB('2026-05-01', INTERVAL k MONTH), '%Y-%m'),
       YEAR(DATE_SUB('2026-05-01', INTERVAL k MONTH)),
       DATE_SUB('2026-05-01', INTERVAL k MONTH),
       LAST_DAY(DATE_SUB('2026-05-01', INTERVAL k MONTH)),
       DATE_SUB(LAST_DAY(DATE_SUB('2026-05-01', INTERVAL k MONTH)), INTERVAL 3 DAY),
       22, 'OPEN'
  FROM m
 WHERE NOT EXISTS (SELECT 1 FROM payroll_periods p
                    WHERE p.period_code = DATE_FORMAT(DATE_SUB('2026-05-01', INTERVAL k MONTH), '%Y-%m'));

-- ---- attendance for every employee and every period -------------------------------------
INSERT IGNORE INTO attendance_summary (period_id, emp_id, working_days, present_days, paid_leave_days,
                                       unpaid_absent_days, overtime_hours, late_minutes)
SELECT p.period_id, e.emp_id, p.working_days, p.working_days - (e.emp_id % 3), 0, e.emp_id % 3,
       (e.emp_id % 7) * 2, e.emp_id % 45
  FROM employees e CROSS JOIN payroll_periods p
 WHERE e.full_name_en LIKE 'Load Employee %';

-- ---- run payroll for the new months (oldest first) and close them ------------------------
DROP PROCEDURE IF EXISTS load_run_open_periods;
DELIMITER $$
CREATE PROCEDURE load_run_open_periods()
BEGIN
  DECLARE v_code CHAR(7);
  DECLARE v_done INT DEFAULT 0;
  DECLARE cur CURSOR FOR SELECT period_code FROM payroll_periods
                          WHERE status = 'OPEN' AND period_code < '2026-05' ORDER BY period_code;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_done = 1;
  OPEN cur;
  periods: LOOP
    FETCH cur INTO v_code;
    IF v_done = 1 THEN LEAVE periods; END IF;
    CALL sp_run_payroll(v_code);
    CALL sp_pay_period(v_code, 'LOAD');
    UPDATE payroll_periods SET status = 'CLOSED' WHERE period_code = v_code;
  END LOOP;
  CLOSE cur;
END$$
DELIMITER ;
CALL load_run_open_periods();
DROP PROCEDURE load_run_open_periods;

SELECT COUNT(*) AS employees FROM employees;
SELECT COUNT(*) AS periods, SUM(status = 'CLOSED') AS closed FROM payroll_periods;
SELECT COUNT(*) AS payslips FROM payslips;
