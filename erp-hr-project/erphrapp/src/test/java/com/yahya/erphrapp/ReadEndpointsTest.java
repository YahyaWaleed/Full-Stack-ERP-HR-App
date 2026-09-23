package com.yahya.erphrapp;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

// open-in-view is off, so any read whose mapper touches a lazy association outside a transaction
// fails with LazyInitializationException (HTTP 500). Every GET endpoint is hit here with real demo-data ids.
class ReadEndpointsTest extends AbstractIntegrationTest {

    @Autowired
    private JdbcTemplate jdbc;

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/v1/employees", "/api/v1/employees/{emp}", "/api/v1/employees?branchId={branch}",
            "/api/v1/employees?deptId={dept}", "/api/v1/employees?jobId={job}", "/api/v1/employees?managerial=true",
            "/api/v1/employees?q=ahmed&status=ACTIVE",
            "/api/v1/employees/{emp}/attendance", "/api/v1/employees/{emp}/contracts",
            "/api/v1/employees/{emp}/leave-balances", "/api/v1/employees/{emp}/leaves",
            "/api/v1/employees/{emp}/salary-components", "/api/v1/employees/{emp}/loans",
            "/api/v1/employees/{emp}/payslips",
            "/api/v1/attendance", "/api/v1/attendance?periodCode=2026-07",
            "/api/v1/contracts", "/api/v1/contracts/{contract}", "/api/v1/dashboard/summary",
            "/api/v1/branches", "/api/v1/branches/{branch}", "/api/v1/departments", "/api/v1/departments/{dept}",
            "/api/v1/jobs", "/api/v1/jobs/{job}",
            "/api/v1/leaves", "/api/v1/leaves/{leave}", "/api/v1/leave-balances/{balance}",
            "/api/v1/leave-types", "/api/v1/leave-types/1",
            "/api/v1/loans", "/api/v1/loans/{loan}", "/api/v1/loans/{loan}/installments",
            "/api/v1/payroll-periods", "/api/v1/payroll-periods?fiscalYear=2026", "/api/v1/payroll-periods/2026-07",
            "/api/v1/payroll-periods/2026-07/payslips", "/api/v1/payroll-periods/2026-07/payments",
            "/api/v1/payroll-payments/{payment}",
            "/api/v1/payroll-settings", "/api/v1/payroll-settings/2026",
            "/api/v1/payslips/{payslip}", "/api/v1/payslips/{payslip}/lines", "/api/v1/payslips/{payslip}/payment",
            "/api/v1/salary-components", "/api/v1/salary-components/1",
            "/api/v1/tax-brackets", "/api/v1/tax-brackets?fiscalYear=2026", "/api/v1/tax-brackets/{bracket}",
            "/api/v1/reports", "/api/v1/audit"
    })
    void readEndpointReturns200(String template) {
        String path = template
                .replace("{emp}", id("SELECT MIN(emp_id) FROM employees WHERE manager_id IS NOT NULL"))
                .replace("{branch}", id("SELECT MIN(branch_id) FROM branches"))
                .replace("{dept}", id("SELECT MIN(dept_id) FROM departments"))
                .replace("{job}", id("SELECT MIN(job_id) FROM job_titles"))
                .replace("{contract}", id("SELECT MIN(contract_id) FROM employee_contracts"))
                .replace("{leave}", id("SELECT MIN(request_id) FROM leave_requests WHERE approver_id IS NOT NULL"))
                .replace("{balance}", id("SELECT MIN(balance_id) FROM leave_balances"))
                .replace("{loan}", id("SELECT MIN(loan_id) FROM loans WHERE approved_by IS NOT NULL"))
                .replace("{payment}", id("SELECT MIN(payment_id) FROM payroll_payments"))
                .replace("{payslip}", id("SELECT MIN(payslip_id) FROM payroll_payments"))
                .replace("{bracket}", id("SELECT MIN(bracket_id) FROM tax_brackets"));

        Response response = get(path, ADMIN);
        assertThat(response.status()).as(path + " -> " + response.body()).isEqualTo(200);
    }

    private String id(String sql) {
        return String.valueOf(jdbc.queryForObject(sql, Long.class));
    }
}
