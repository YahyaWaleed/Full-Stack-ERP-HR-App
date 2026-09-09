/* =====================================================================
   ERP HR & Payroll  --  ready-made reports / sample queries
   Usage:  mysql -u root --default-character-set=utf8mb4 -t erp_hr < erp_hr_reports.sql
   ===================================================================== */
USE erp_hr;

-- 1) Employee directory ------------------------------------------------
SELECT emp_code, full_name_ar, department, job_title, job_grade, branch,
       manager, hire_date, years_of_service, basic_salary, emp_status
FROM v_employee_directory ORDER BY emp_code;

-- 2) Headcount & average salary per department -------------------------
SELECT * FROM v_headcount_by_dept ORDER BY headcount DESC;

-- 3) Payroll register for one month ------------------------------------
SELECT emp_code, full_name_ar, department, basic_salary, gross_pay,
       insurance_employee, income_tax, total_deductions, net_pay, status
FROM v_payslip_header WHERE period_code = '2026-07'
ORDER BY net_pay DESC;

-- 4) A single printable payslip ----------------------------------------
SELECT comp_type AS النوع, comp_name_ar AS البند, amount AS المبلغ, calc_note AS الحساب
FROM v_payslip_detail
WHERE period_code = '2026-07' AND emp_code = 'EMP-0020';

SELECT payslip_no, period_code, full_name_ar, job_title, worked_days, absent_days,
       overtime_hours, gross_pay, total_deductions, net_pay, total_company_cost
FROM v_payslip_header WHERE period_code = '2026-07' AND emp_code = 'EMP-0020';

-- 5) Payroll cost per department ---------------------------------------
SELECT * FROM v_payroll_cost_by_dept WHERE period_code = '2026-07'
ORDER BY company_cost DESC;

-- 6) Month-over-month payroll trend ------------------------------------
SELECT pp.period_code, COUNT(*) AS employees,
       ROUND(SUM(p.total_earnings),2) AS gross,
       ROUND(SUM(p.total_deductions),2) AS deductions,
       ROUND(SUM(p.net_pay),2) AS net,
       ROUND(SUM(p.total_earnings + p.insurance_employer),2) AS company_cost
FROM payslips p JOIN payroll_periods pp USING (period_id)
GROUP BY pp.period_code ORDER BY pp.period_code;

-- 7) Bank transfer file (paid month) -----------------------------------
SELECT e.full_name_en, e.bank_name, e.bank_account, pay.amount, pay.reference, pay.paid_on
FROM payroll_payments pay
JOIN payslips p ON p.payslip_id = pay.payslip_id
JOIN payroll_periods pp ON pp.period_id = p.period_id
JOIN employees e ON e.emp_id = p.emp_id
WHERE pp.period_code = '2026-07' AND pay.method = 'BANK'
ORDER BY e.emp_code;

-- 8) Tax & insurance liability towards the authorities ------------------
SELECT pp.period_code,
       ROUND(SUM(p.income_tax),2)                              AS income_tax_due,
       ROUND(SUM(p.insurance_employee),2)                      AS insurance_employee_share,
       ROUND(SUM(p.insurance_employer),2)                      AS insurance_employer_share,
       ROUND(SUM(p.insurance_employee + p.insurance_employer),2) AS total_insurance_due
FROM payslips p JOIN payroll_periods pp USING (period_id)
GROUP BY pp.period_code;

-- 9) Leave balances & consumption --------------------------------------
SELECT * FROM v_leave_balances
WHERE fiscal_year = 2026 AND leave_type = 'إجازة سنوية'
ORDER BY remaining_days;

-- 10) Leave request log + still pending --------------------------------
SELECT * FROM v_leave_requests_log ORDER BY start_date;
SELECT * FROM v_leave_requests_log WHERE status = 'PENDING';

-- 11) Top overtime earners ---------------------------------------------
SELECT e.emp_code, e.full_name_ar, SUM(a.overtime_hours) AS ot_hours,
       ROUND(SUM(l.amount),2) AS ot_paid
FROM attendance_summary a
JOIN employees e ON e.emp_id = a.emp_id
JOIN payslips p  ON p.emp_id = a.emp_id AND p.period_id = a.period_id
JOIN payslip_lines l ON l.payslip_id = p.payslip_id AND l.comp_code = 'OT'
GROUP BY e.emp_code, e.full_name_ar
ORDER BY ot_paid DESC LIMIT 10;

-- 12) Absence / lateness watch-list ------------------------------------
SELECT e.emp_code, e.full_name_ar, d.name_ar AS department,
       SUM(a.unpaid_absent_days) AS unpaid_days, SUM(a.late_minutes) AS late_minutes
FROM attendance_summary a
JOIN employees e ON e.emp_id = a.emp_id
JOIN departments d ON d.dept_id = e.dept_id
GROUP BY e.emp_code, e.full_name_ar, d.name_ar
HAVING unpaid_days > 0 OR late_minutes > 150
ORDER BY unpaid_days DESC, late_minutes DESC;

-- 13) Outstanding loans -------------------------------------------------
SELECT * FROM v_active_loans WHERE status = 'ACTIVE' ORDER BY remaining_balance DESC;

-- 14) Salary structure of one employee ---------------------------------
SELECT sc.code, sc.name_ar, sc.comp_type, sc.calc_type,
       IFNULL(esc.amount, sc.default_value)     AS amount,
       IFNULL(esc.percentage, sc.default_value) AS pct,
       esc.effective_from, esc.effective_to
FROM employee_salary_components esc
JOIN salary_components sc ON sc.comp_id = esc.comp_id
WHERE esc.emp_id = 20
ORDER BY sc.comp_type DESC, sc.print_order;

-- 15) Contracts expiring within 12 months -------------------------------
SELECT e.emp_code, e.full_name_ar, c.contract_no, c.contract_type,
       c.start_date, c.end_date, DATEDIFF(c.end_date, CURDATE()) AS days_left
FROM employee_contracts c JOIN employees e ON e.emp_id = c.emp_id
WHERE c.status = 'ACTIVE' AND c.end_date IS NOT NULL
  AND c.end_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 12 MONTH)
ORDER BY c.end_date;
