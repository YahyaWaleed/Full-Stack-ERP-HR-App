package com.yahya.erphrapp.report.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReportRepositoryImpl implements ReportRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> getEmployeeDirectory() {
        return entityManager.createNativeQuery("""
                SELECT emp_code, full_name_ar, full_name_en, gender, age,
                       department, job_title, job_grade, branch, manager,
                       hire_date, years_of_service, emp_status, contract_type,
                       basic_salary, email, mobile
                FROM v_employee_directory
                ORDER BY emp_code
                """).getResultList();
    }

    @Override
    public List<Object[]> getHeadcountByDept() {
        return entityManager.createNativeQuery("""
                SELECT code, department, branch, headcount, males, females,
                       avg_service_years, avg_basic_salary
                FROM v_headcount_by_dept
                ORDER BY headcount DESC
                """).getResultList();
    }

    @Override
    public List<Object[]> getPayrollRegister(String periodCode) {
        return entityManager.createNativeQuery("""
                SELECT payslip_id, payslip_no, period_code, pay_date,
                       emp_code, full_name_ar, department, job_title,
                       basic_salary, gross_pay, total_deductions,
                       insurance_employee, income_tax, net_pay,
                       worked_days, absent_days, overtime_hours,
                       insurance_employer, total_company_cost,
                       status, currency
                FROM v_payslip_header
                WHERE period_code = :periodCode
                ORDER BY net_pay DESC
                """)
                .setParameter("periodCode", periodCode)
                .getResultList();
    }

    @Override
    public List<Object[]> getPayrollCostByDept(String periodCode) {
        return entityManager.createNativeQuery("""
                SELECT period_code, department, employees, total_basic,
                       total_gross, total_tax, insurance_employee,
                       insurance_employer, total_net, company_cost
                FROM v_payroll_cost_by_dept
                WHERE period_code = :periodCode
                ORDER BY company_cost DESC
                """)
                .setParameter("periodCode", periodCode)
                .getResultList();
    }

    @Override
    public List<Object[]> getPayrollTrend() {
        return entityManager.createNativeQuery("""
                SELECT pp.period_code,
                       COUNT(*) AS employees,
                       ROUND(SUM(p.total_earnings), 2) AS gross,
                       ROUND(SUM(p.total_deductions), 2) AS deductions,
                       ROUND(SUM(p.net_pay), 2) AS net,
                       ROUND(SUM(p.total_earnings + p.insurance_employer), 2) AS company_cost
                FROM payslips p
                JOIN payroll_periods pp USING (period_id)
                GROUP BY pp.period_code
                ORDER BY pp.period_code
                """).getResultList();
    }

    @Override
    public List<Object[]> getTaxInsuranceLiability() {
        return entityManager.createNativeQuery("""
                SELECT pp.period_code,
                       ROUND(SUM(p.income_tax), 2) AS income_tax_due,
                       ROUND(SUM(p.insurance_employee), 2) AS insurance_employee_share,
                       ROUND(SUM(p.insurance_employer), 2) AS insurance_employer_share,
                       ROUND(SUM(p.insurance_employee + p.insurance_employer), 2)
                           AS total_insurance_due
                FROM payslips p
                JOIN payroll_periods pp USING (period_id)
                GROUP BY pp.period_code
                """).getResultList();
    }

    @Override
    public List<Object[]> getBankTransfer(String periodCode) {
        return entityManager.createNativeQuery("""
                SELECT e.full_name_en,
                       e.bank_name,
                       e.bank_account,
                       pay.amount,
                       pay.reference,
                       pay.paid_on
                FROM payroll_payments pay
                JOIN payslips p
                    ON p.payslip_id = pay.payslip_id
                JOIN payroll_periods pp
                    ON pp.period_id = p.period_id
                JOIN employees e
                    ON e.emp_id = p.emp_id
                WHERE pp.period_code = :periodCode
                  AND pay.method = 'BANK'
                ORDER BY e.emp_code
                """)
                .setParameter("periodCode", periodCode)
                .getResultList();
    }

    @Override
    public List<Object[]> getLeaveBalances(int fiscalYear) {
        return entityManager.createNativeQuery("""
                SELECT emp_code,
                       full_name_ar,
                       department,
                       leave_type,
                       fiscal_year,
                       entitled_days,
                       carried_forward,
                       used_days,
                       remaining_days
                FROM v_leave_balances
                WHERE fiscal_year = :fiscalYear
                ORDER BY remaining_days
                """)
                .setParameter("fiscalYear", fiscalYear)
                .getResultList();
    }

    @Override
    public List<Object[]> getLeaveRequestLog(String status) {
        return entityManager.createNativeQuery("""
                SELECT request_id,
                       emp_code,
                       full_name_ar,
                       leave_type,
                       start_date,
                       end_date,
                       days_count,
                       status,
                       approved_by,
                       applied_on,
                       decided_on,
                       reason
                FROM v_leave_requests_log
                WHERE (:status IS NULL OR status = :status)
                ORDER BY start_date
                """)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public List<Object[]> getOvertimeTop10() {
        return entityManager.createNativeQuery("""
                SELECT e.emp_code,
                       e.full_name_ar,
                       SUM(a.overtime_hours) AS ot_hours,
                       ROUND(SUM(l.amount), 2) AS ot_paid
                FROM attendance_summary a
                JOIN employees e
                    ON e.emp_id = a.emp_id
                JOIN payslips p
                    ON p.emp_id = a.emp_id
                   AND p.period_id = a.period_id
                JOIN payslip_lines l
                    ON l.payslip_id = p.payslip_id
                   AND l.comp_code = 'OT'
                GROUP BY e.emp_code, e.full_name_ar
                ORDER BY ot_paid DESC
                LIMIT 10
                """).getResultList();
    }

    @Override
    public List<Object[]> getAbsenceWatchlist() {
        return entityManager.createNativeQuery("""
                SELECT e.emp_code,
                       e.full_name_ar,
                       d.name_ar,
                       SUM(a.unpaid_absent_days) AS unpaid_days,
                       SUM(a.late_minutes) AS late_minutes
                FROM attendance_summary a
                JOIN employees e
                    ON e.emp_id = a.emp_id
                JOIN departments d
                    ON d.dept_id = e.dept_id
                GROUP BY e.emp_code, e.full_name_ar, d.name_ar
                HAVING SUM(a.unpaid_absent_days) > 0
                    OR SUM(a.late_minutes) > 150
                ORDER BY SUM(a.unpaid_absent_days) DESC,
                         SUM(a.late_minutes) DESC
                """).getResultList();
    }

    @Override
    public List<Object[]> getActiveLoans() {
        return entityManager.createNativeQuery("""
                SELECT loan_id,
                       emp_code,
                       full_name_ar,
                       loan_type,
                       principal_amount,
                       installments_count,
                       monthly_installment,
                       remaining_balance,
                       paid_pct,
                       start_period,
                       status
                FROM v_active_loans
                WHERE status = 'ACTIVE'
                ORDER BY remaining_balance DESC
                """).getResultList();
    }

    @Override
    public List<Object[]> getContractsExpiring(int months) {
        return entityManager.createNativeQuery("""
                SELECT e.emp_code,
                       e.full_name_ar,
                       c.contract_no,
                       c.contract_type,
                       c.start_date,
                       c.end_date,
                       DATEDIFF(c.end_date, CURDATE()) AS days_left
                FROM employee_contracts c
                JOIN employees e
                    ON e.emp_id = c.emp_id
                WHERE c.status = 'ACTIVE'
                  AND c.end_date IS NOT NULL
                  AND c.end_date BETWEEN CURDATE()
                      AND DATE_ADD(CURDATE(), INTERVAL :months MONTH)
                ORDER BY c.end_date
                """)
                .setParameter("months", months)
                .getResultList();
    }

    @Override
    public List<Object[]> getTopAttendance(String periodCode) {
        return entityManager.createNativeQuery("""
            SELECT e.emp_code,
                   e.full_name_ar,
                   a.present_days
            FROM attendance_summary a
            JOIN employees e
                ON e.emp_id = a.emp_id
            JOIN payroll_periods pp
                ON pp.period_id = a.period_id
            WHERE pp.period_code = :periodCode
            ORDER BY a.present_days DESC
            LIMIT 10
            """)
                .setParameter("periodCode", periodCode)
                .getResultList();
    }

    @Override
    public List<Object[]> getTopNetSalary(String periodCode) {
        return entityManager.createNativeQuery("""
            SELECT e.emp_code,
                   e.full_name_ar,
                   p.net_pay
            FROM payslips p
            JOIN employees e
                ON e.emp_id = p.emp_id
            JOIN payroll_periods pp
                ON pp.period_id = p.period_id
            WHERE pp.period_code = :periodCode
            ORDER BY p.net_pay DESC
            LIMIT 10
            """)
                .setParameter("periodCode", periodCode)
                .getResultList();
    }
}