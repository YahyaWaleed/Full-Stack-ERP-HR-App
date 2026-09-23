package com.yahya.erphrapp.loans;

import com.yahya.erphrapp.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

// only ACTIVE loans can be closed or cancelled (review 10.4), and inconsistent loan terms are rejected
class LoanStateTest extends AbstractIntegrationTest {

    @Test
    void closedLoanCannotBeCancelledAndCancelledLoanCannotBeClosed() {
        long closed = createLoan();
        assertThat(post("/api/v1/loans/" + closed + "/close", null, ADMIN).status()).isEqualTo(200);
        assertThat(post("/api/v1/loans/" + closed + "/cancel", null, ADMIN).status()).isEqualTo(409);

        long cancelled = createLoan();
        assertThat(post("/api/v1/loans/" + cancelled + "/cancel", null, ADMIN).status()).isEqualTo(200);
        Response close = post("/api/v1/loans/" + cancelled + "/close", null, ADMIN);
        assertThat(close.status()).isEqualTo(409);
        assertThat(close.json().get("message").toString()).contains("Only an ACTIVE loan");
    }

    @Test
    void installmentsThatDontCoverThePrincipalAre400() {
        Map<String, Object> body = loan();
        body.put("installmentsCount", 2); // 2 x 1000 < 3000
        assertThat(post("/api/v1/loans", body, ADMIN).status()).isEqualTo(400);
    }

    @Test
    void unknownLoanTypeIs400() {
        Map<String, Object> body = loan();
        body.put("type", "GAMBLING");
        assertThat(post("/api/v1/loans", body, ADMIN).status()).isEqualTo(400);
    }

    private long createLoan() {
        Response r = post("/api/v1/loans", loan(), ADMIN);
        assertThat(r.status()).as(r.body()).isEqualTo(200);
        return ((Number) r.json().get("id")).longValue();
    }

    private static Map<String, Object> loan() {
        return new HashMap<>(Map.of("empId", 5, "type", "ADVANCE", "principalAmount", 3000, "installmentsCount", 3,
                "monthlyInstallment", 1000, "startPeriod", "2026-10", "requestDate", "2026-09-01"));
    }
}
