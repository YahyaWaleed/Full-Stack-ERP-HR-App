package com.yahya.erphrapp.audit;

import com.yahya.erphrapp.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

// who opened, ran and paid payroll is recorded and readable by admins only (review 9.9)
class AuditTrailTest extends AbstractIntegrationTest {

    @Test
    void payrollCycleIsAudited() {
        Map<String, Object> period = Map.of("periodCode", "2026-12", "fiscalYear", 2026, "startDate", "2026-12-01",
                "endDate", "2026-12-31", "payDate", "2026-12-27", "workingDays", 22);
        assertThat(post("/api/v1/payroll-periods", period, ADMIN).status()).isEqualTo(200);
        assertThat(post("/api/v1/payroll-periods", period, ADMIN).status()).isEqualTo(409); // duplicate code
        assertThat(post("/api/v1/payroll-periods/2026-12/run", null, ADMIN).status()).isEqualTo(200);
        assertThat(post("/api/v1/payroll-periods/2026-12/pay?method=BANK", null, ADMIN).status()).isEqualTo(200);

        Map<String, Object> page = get("/api/v1/audit?targetType=PAYROLL_PERIOD&targetId=2026-12", ADMIN).json();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> entries = (List<Map<String, Object>>) page.get("content");
        assertThat(entries).extracting(e -> e.get("action"))
                .containsExactly("PAYROLL_PAID", "PAYROLL_RUN", "PERIOD_OPENED"); // newest first
        assertThat(entries).allSatisfy(e -> assertThat(e.get("actor")).isEqualTo("test-hr_admin"));
        assertThat(entries.get(1).get("details").toString()).contains("payslips=");
    }

    @Test
    void invalidPaymentMethodIs400() {
        assertThat(post("/api/v1/payroll-periods/2026-08/pay?method=BITCOIN", null, ADMIN).status()).isEqualTo(400);
    }

    @Test
    void hrUserCannotReadTheAuditLog() {
        assertThat(get("/api/v1/audit", USER).status()).isEqualTo(403);
    }
}
