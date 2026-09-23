package com.yahya.erphrapp.leaves;

import com.yahya.erphrapp.AbstractIntegrationTest;
import com.yahya.erphrapp.TestData;
import com.yahya.erphrapp.exception.ConflictException;
import com.yahya.erphrapp.leaves.service.LeaveRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

// leave-type rules and the approval race (review 10.3, 7.9); leave types: 1 annual, 2 sick (needs attachment), 3 casual (max 2)
class LeaveRulesTest extends AbstractIntegrationTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private LeaveRequestService leaveRequestService;

    private long empId;

    @BeforeEach
    void newEmployee() {
        Response created = post("/api/v1/employees",
                TestData.employee(uniqueNationalId(), LocalDate.of(2026, 1, 1), TestData.contract(LocalDate.of(2026, 1, 1), null)), ADMIN);
        assertThat(created.status()).as(created.body()).isEqualTo(200);
        empId = ((Number) created.json().get("id")).longValue();
    }

    @Test
    void weekendsAndPublicHolidaysAreNotCounted() {
        // Sun 4 Oct .. Thu 8 Oct 2026; Tue 6 Oct is Armed Forces Day
        Response r = request(1, "2026-10-04", "2026-10-08", null);
        assertThat(r.status()).as(r.body()).isEqualTo(200);
        assertThat(new BigDecimal(r.json().get("daysCount").toString())).isEqualByComparingTo("4");
    }

    @Test
    void rangeWithNoWorkingDaysIs400() {
        assertThat(request(1, "2026-10-09", "2026-10-10", null).status()).isEqualTo(400); // Fri + Sat
    }

    @Test
    void maxConsecutiveDaysIsEnforced() {
        Response r = request(3, "2026-10-11", "2026-10-13", null); // casual leave, 3 working days, max 2
        assertThat(r.status()).isEqualTo(400);
        assertThat(r.json().get("message").toString()).contains("at most 2");
    }

    @Test
    void attachmentIsRequiredWhereTheTypeSaysSo() {
        assertThat(request(2, "2026-10-11", "2026-10-12", null).status()).isEqualTo(400);
        assertThat(request(2, "2026-10-11", "2026-10-12", "MED-2026-0142").status()).isEqualTo(200);
    }

    @Test
    void overlappingRequestsAreRejected() {
        assertThat(request(1, "2026-11-01", "2026-11-05", null).status()).isEqualTo(200);
        Response overlap = request(1, "2026-11-04", "2026-11-10", null);
        assertThat(overlap.status()).isEqualTo(409);
        assertThat(overlap.json().get("message").toString()).contains("overlap");
    }

    @Test
    void decidedRequestsCannotBeDecidedAgain() {
        long id = ((Number) request(1, "2026-12-06", "2026-12-07", null).json().get("id")).longValue();
        assertThat(post("/api/v1/leaves/" + id + "/approve", null, ADMIN).status()).isEqualTo(200);
        assertThat(post("/api/v1/leaves/" + id + "/approve", null, ADMIN).status()).isEqualTo(409);
        assertThat(post("/api/v1/leaves/" + id + "/cancel", null, ADMIN).status()).isEqualTo(409);
    }

    @Test
    void concurrentApprovalsCannotOverdrawTheBalance() throws Exception {
        // 21 days of annual leave; two pending requests of 15 days each -- only one may be approved
        List<Long> ids = new ArrayList<>();
        for (String start : List.of("2026-06-01", "2026-08-01")) {
            jdbc.update("""
                    INSERT INTO leave_requests (emp_id, type_id, start_date, end_date, days_count, status, applied_on)
                    VALUES (?, 1, ?, DATE_ADD(?, INTERVAL 20 DAY), 15, 'PENDING', CURDATE())""", empId, start, start);
            ids.add(jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class));
        }

        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch go = new CountDownLatch(1);
        List<Future<String>> results = new ArrayList<>();
        for (Long id : ids) {
            results.add(pool.submit(() -> {
                go.await();
                try {
                    leaveRequestService.approveRequest(id);
                    return "approved";
                } catch (ConflictException e) {
                    return "conflict";
                }
            }));
        }
        go.countDown();
        List<String> outcomes = new ArrayList<>();
        for (Future<String> f : results) {
            outcomes.add(f.get(30, TimeUnit.SECONDS));
        }
        pool.shutdown();

        assertThat(outcomes).containsExactlyInAnyOrder("approved", "conflict");
        BigDecimal remaining = jdbc.queryForObject(
                "SELECT remaining_days FROM leave_balances WHERE emp_id = ? AND type_id = 1 AND fiscal_year = 2026", BigDecimal.class, empId);
        assertThat(remaining).isEqualByComparingTo("6");
    }

    private Response request(int typeId, String start, String end, String attachmentRef) {
        Map<String, Object> body = new HashMap<>(Map.of("typeId", typeId, "startDate", start, "endDate", end));
        if (attachmentRef != null) body.put("attachmentRef", attachmentRef);
        return post("/api/v1/employees/" + empId + "/leaves", body, ADMIN);
    }
}
