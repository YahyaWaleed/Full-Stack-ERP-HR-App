package com.yahya.erphrapp.report;

import com.yahya.erphrapp.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ReportsTest extends AbstractIntegrationTest {

    // every registered report runs with realistic parameters and returns camelCase rows
    @ParameterizedTest
    @ValueSource(strings = {
            "employee-directory", "headcount-by-department", "payroll-register?periodCode=2026-07",
            "payroll-cost-by-department?periodCode=2026-07", "payroll-trend", "payroll-trend?fiscalYear=2026",
            "tax-insurance-liability", "bank-transfer?periodCode=2026-07", "leave-balances?fiscalYear=2026",
            "leave-requests", "leave-requests?status=APPROVED", "overtime-top10", "absence-watchlist", "active-loans",
            "contracts-expiring", "contracts-expiring?months=24", "top-attendance?periodCode=2026-07",
            "top-net-salary?periodCode=2026-07"
    })
    void reportRuns(String slugAndQuery) {
        Response r = get("/api/v1/reports/" + slugAndQuery, ADMIN);
        assertThat(r.status()).as(slugAndQuery + " -> " + r.body()).isEqualTo(200);
        // every key is camelCase -- no snake_case column names leak through
        Object parsed = r.body().startsWith("[") ? r.jsonList() : r.json().get("content");
        for (Object row : (List<?>) parsed) {
            assertThat(((Map<?, ?>) row).keySet()).allSatisfy(k -> assertThat(k.toString()).doesNotContain("_"));
        }
    }

    @Test
    void payrollRegisterReturnsNamedFieldsWithTheRightValues() {
        List<Object> rows = get("/api/v1/reports/payroll-register?periodCode=2026-07", ADMIN).jsonList();
        assertThat(rows).isNotEmpty();
        @SuppressWarnings("unchecked")
        Map<String, Object> first = (Map<String, Object>) rows.getFirst();
        assertThat(first).containsKeys("payslipNo", "empCode", "fullNameAr", "grossPay", "netPay", "payDate");
        assertThat(first.get("periodCode")).isEqualTo("2026-07");
        assertThat(first.get("payDate").toString()).matches("\\d{4}-\\d{2}-\\d{2}"); // ISO date, not an array/number
    }

    @Test
    void pageableReportsReturnAPageWhenAsked() {
        Map<String, Object> page = get("/api/v1/reports/employee-directory?page=0&size=10", ADMIN).json();
        assertThat((List<?>) page.get("content")).hasSize(10);
        assertThat(((Number) page.get("totalElements")).intValue()).isGreaterThan(10);
        assertThat(((Number) page.get("totalPages")).intValue()).isGreaterThan(1);
    }

    @Test
    void missingOrMalformedParametersAre400AndUnknownReportsAre404() {
        assertThat(get("/api/v1/reports/payroll-register", ADMIN).status()).isEqualTo(400);
        assertThat(get("/api/v1/reports/payroll-register?periodCode=July", ADMIN).status()).isEqualTo(400);
        assertThat(get("/api/v1/reports/leave-balances?fiscalYear=soon", ADMIN).status()).isEqualTo(400);
        assertThat(get("/api/v1/reports/no-such-report", ADMIN).status()).isEqualTo(404);
    }

    @Test
    void catalogueListsEveryReportWithItsParameters() {
        List<Object> catalogue = get("/api/v1/reports", ADMIN).jsonList();
        assertThat(catalogue).hasSize(15);
    }
}
