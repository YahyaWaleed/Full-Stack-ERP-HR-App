package com.yahya.erphrapp;

import com.yahya.erphrapp.authentication.security.JwtUtil;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

// open-in-view is off, so any read whose mapper touches a lazy association outside a transaction
// fails with LazyInitializationException (HTTP 500). Every GET endpoint is hit here with real demo-data ids.
class ReadEndpointsTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JdbcTemplate jdbc;

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/employees", "/api/employees/{emp}", "/api/employees/{branch}/employees",
            "/api/employees/department/{dept}", "/api/employees/job/{job}",
            "/api/employees/{emp}/attendance", "/api/employees/{emp}/contracts",
            "/api/employees/{emp}/leave-balances", "/api/employees/{emp}/leaves",
            "/api/employees/{emp}/salary-components", "/api/employees/{emp}/loans",
            "/api/attendance", "/api/contracts", "/api/contracts/{contract}", "/api/dashboard/summary",
            "/api/branches", "/api/branches/{branch}", "/api/departments", "/api/departments/{dept}",
            "/api/jobs", "/api/jobs/{job}",
            "/api/leaves", "/api/leaves/{leave}", "/api/leave-balances/{balance}",
            "/api/leave-types", "/api/leave-types/1",
            "/api/loans", "/api/loans/{loan}", "/api/loans/{loan}/installments",
            "/api/payroll-periods", "/api/payroll-periods/2026-07", "/api/payroll-periods/fiscal-year/2026",
            "/api/payroll-periods/2026-07/payments", "/api/payroll-payments/{payment}",
            "/api/payroll-settings", "/api/payroll-settings/2026",
            "/api/payslips/employee/{emp}", "/api/payslips/month/2026-07", "/api/payslips/{payslip}",
            "/api/payslips/{payslip}/lines", "/api/payslips/{payslip}/payment",
            "/api/salary-components", "/api/salary-components/1",
            "/api/tax-brackets", "/api/tax-brackets/fiscal-year/2026", "/api/tax-brackets/{bracket}"
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

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtUtil.generateToken("admin", "HR_ADMIN"));
        try {
            var response = new RestTemplate().exchange(
                    "http://localhost:" + port + path, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            assertThat(response.getStatusCode()).as(path).isEqualTo(HttpStatus.OK);
        } catch (HttpStatusCodeException ex) {
            throw new AssertionError(path + " returned " + ex.getStatusCode() + ": " + ex.getResponseBodyAsString());
        }
    }

    private String id(String sql) {
        return String.valueOf(jdbc.queryForObject(sql, Long.class));
    }
}
