package com.yahya.erphrapp.report;

import com.yahya.erphrapp.report.ReportDefinition.Param;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.yahya.erphrapp.report.ReportDefinition.ParamType.INTEGER;
import static com.yahya.erphrapp.report.ReportDefinition.ParamType.PERIOD_CODE;
import static com.yahya.erphrapp.report.ReportDefinition.ParamType.TEXT;

// Every report in one place (review 4.2). Adding a report = adding one entry here; the endpoint
// (GET /api/v1/reports/{slug}), parameter validation and row mapping are shared.
@Component
public class ReportRegistry {

    private final Map<String, ReportDefinition> reports = new LinkedHashMap<>();

    public ReportRegistry() {
        add("employee-directory", "Employee Directory", """
                SELECT emp_code, full_name_ar, full_name_en, gender, age, department, job_title,
                       job_grade, branch, manager, hire_date, years_of_service, emp_status,
                       contract_type, basic_salary, email, mobile
                FROM v_employee_directory ORDER BY emp_code""", List.of(), true);

        add("headcount-by-department", "Headcount by Department", """
                SELECT code, department, branch, headcount, males, females,
                       avg_service_years, avg_basic_salary
                FROM v_headcount_by_dept ORDER BY headcount DESC""", List.of(), false);

        add("payroll-register", "Payroll Register", """
                SELECT payslip_id, payslip_no, period_code, pay_date, emp_code, full_name_ar,
                       department, job_title, basic_salary, gross_pay, total_deductions,
                       insurance_employee, income_tax, net_pay, worked_days, absent_days,
                       overtime_hours, insurance_employer, total_company_cost, status, currency
                FROM v_payslip_header WHERE period_code = :periodCode ORDER BY net_pay DESC""",
                List.of(Param.required("periodCode", PERIOD_CODE)), true);

        add("payroll-cost-by-department", "Payroll Cost by Department", """
                SELECT period_code, department, employees, total_basic, total_gross, total_tax,
                       insurance_employee, insurance_employer, total_net, company_cost
                FROM v_payroll_cost_by_dept WHERE period_code = :periodCode ORDER BY company_cost DESC""",
                List.of(Param.required("periodCode", PERIOD_CODE)), false);

        // limited to one fiscal year (default: the current one) so the query doesn't grow with every month on record
        add("payroll-trend", "Payroll Trend", """
                SELECT pp.period_code AS period_code, COUNT(*) AS employees,
                       ROUND(SUM(p.total_earnings),2) AS gross,
                       ROUND(SUM(p.total_deductions),2) AS deductions,
                       ROUND(SUM(p.net_pay),2) AS net,
                       ROUND(SUM(p.total_earnings + p.insurance_employer),2) AS company_cost
                FROM payslips p JOIN payroll_periods pp USING (period_id)
                WHERE pp.fiscal_year = COALESCE(:fiscalYear, YEAR(CURDATE()))
                GROUP BY pp.period_code ORDER BY pp.period_code""",
                List.of(Param.optional("fiscalYear", INTEGER, null)), false);

        add("tax-insurance-liability", "Tax & Insurance Liability", """
                SELECT pp.period_code AS period_code,
                       ROUND(SUM(p.income_tax),2) AS income_tax_due,
                       ROUND(SUM(p.insurance_employee),2) AS insurance_employee_share,
                       ROUND(SUM(p.insurance_employer),2) AS insurance_employer_share,
                       ROUND(SUM(p.insurance_employee + p.insurance_employer),2) AS total_insurance_due
                FROM payslips p JOIN payroll_periods pp USING (period_id)
                WHERE pp.fiscal_year = COALESCE(:fiscalYear, YEAR(CURDATE()))
                GROUP BY pp.period_code ORDER BY pp.period_code""",
                List.of(Param.optional("fiscalYear", INTEGER, null)), false);

        add("bank-transfer", "Bank Transfer File", """
                SELECT e.full_name_en, e.bank_name, e.bank_account, pay.amount, pay.reference, pay.paid_on
                FROM payroll_payments pay JOIN payslips p ON p.payslip_id = pay.payslip_id
                JOIN payroll_periods pp ON pp.period_id = p.period_id JOIN employees e ON e.emp_id = p.emp_id
                WHERE pp.period_code = :periodCode AND pay.method = 'BANK' ORDER BY e.emp_code""",
                List.of(Param.required("periodCode", PERIOD_CODE)), false);

        add("leave-balances", "Leave Balances", """
                SELECT emp_code, full_name_ar, department, leave_type, fiscal_year, entitled_days,
                       carried_forward, used_days, remaining_days
                FROM v_leave_balances WHERE fiscal_year = :fiscalYear ORDER BY remaining_days""",
                List.of(Param.required("fiscalYear", INTEGER)), true);

        add("leave-requests", "Leave Request Log", """
                SELECT request_id, emp_code, full_name_ar, leave_type, start_date, end_date, days_count,
                       status, approved_by, applied_on, decided_on, reason
                FROM v_leave_requests_log WHERE (:status IS NULL OR status = :status) ORDER BY start_date""",
                List.of(Param.optional("status", TEXT, null)), true);

        add("overtime-top10", "Top 10 Overtime", """
                SELECT e.emp_code, e.full_name_ar, SUM(a.overtime_hours) AS ot_hours,
                       ROUND(SUM(l.amount),2) AS ot_paid
                FROM attendance_summary a JOIN employees e ON e.emp_id = a.emp_id
                JOIN payslips p ON p.emp_id = a.emp_id AND p.period_id = a.period_id
                JOIN payslip_lines l ON l.payslip_id = p.payslip_id AND l.comp_code = 'OT'
                GROUP BY e.emp_code, e.full_name_ar ORDER BY ot_paid DESC LIMIT 10""", List.of(), false);

        add("absence-watchlist", "Absence Watchlist", """
                SELECT e.emp_code, e.full_name_ar, d.name_ar AS department,
                       SUM(a.unpaid_absent_days) AS unpaid_days, SUM(a.late_minutes) AS late_minutes
                FROM attendance_summary a JOIN employees e ON e.emp_id = a.emp_id
                JOIN departments d ON d.dept_id = e.dept_id
                GROUP BY e.emp_code, e.full_name_ar, d.name_ar
                HAVING unpaid_days > 0 OR late_minutes > 150
                ORDER BY unpaid_days DESC, late_minutes DESC""", List.of(), false);

        add("active-loans", "Active Loans", """
                SELECT loan_id, emp_code, full_name_ar, loan_type, principal_amount, installments_count,
                       monthly_installment, remaining_balance, paid_pct, start_period, status
                FROM v_active_loans WHERE status = 'ACTIVE' ORDER BY remaining_balance DESC""", List.of(), false);

        add("contracts-expiring", "Contracts Expiring", """
                SELECT e.emp_id, e.emp_code, e.full_name_ar, c.contract_no, c.contract_type, c.start_date,
                       c.end_date, DATEDIFF(c.end_date, CURDATE()) AS days_left
                FROM employee_contracts c JOIN employees e ON e.emp_id = c.emp_id
                WHERE c.status = 'ACTIVE' AND c.end_date IS NOT NULL
                AND c.end_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL :months MONTH)
                ORDER BY c.end_date""",
                List.of(Param.optional("months", INTEGER, "12")), false);

        add("top-attendance", "Top 10 Attendance", """
                SELECT e.emp_code, e.full_name_ar, a.present_days
                FROM attendance_summary a
                JOIN employees e ON e.emp_id = a.emp_id
                JOIN payroll_periods pp ON pp.period_id = a.period_id
                WHERE pp.period_code = :periodCode
                ORDER BY a.present_days DESC LIMIT 10""",
                List.of(Param.required("periodCode", PERIOD_CODE)), false);

        add("top-net-salary", "Top 10 Net Salary", """
                SELECT e.emp_code, e.full_name_ar, p.net_pay
                FROM payslips p
                JOIN employees e ON e.emp_id = p.emp_id
                JOIN payroll_periods pp ON pp.period_id = p.period_id
                WHERE pp.period_code = :periodCode
                ORDER BY p.net_pay DESC LIMIT 10""",
                List.of(Param.required("periodCode", PERIOD_CODE)), false);
    }

    public Optional<ReportDefinition> find(String slug) {
        return Optional.ofNullable(reports.get(slug));
    }

    public List<ReportDefinition> all() {
        return List.copyOf(reports.values());
    }

    private void add(String slug, String title, String sql, List<Param> params, boolean pageable) {
        reports.put(slug, new ReportDefinition(slug, title, sql, params, pageable));
    }
}
