package com.yahya.erphrapp.employee;

import com.yahya.erphrapp.AbstractIntegrationTest;
import com.yahya.erphrapp.TestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeLifecycleTest extends AbstractIntegrationTest {

    @Autowired
    private JdbcTemplate jdbc;

    // ---- create (5.4, 5.5, 5.6) -----------------------------------------------------------------

    @Test
    void createAssignsCodeInTheDatabaseAndOpensContractAndLeaveBalancesInOneGo() {
        Map<String, Object> created = create(TestData.contract(LocalDate.of(2026, 1, 1), null));

        long id = ((Number) created.get("id")).longValue();
        assertThat((String) created.get("empCode")).matches("EMP-\\d{4,}");
        assertThat(created.get("maritalStatus")).isEqualTo("SINGLE");      // default applied
        assertThat(created.get("paymentMethod")).isEqualTo("BANK");

        String contractNo = jdbc.queryForObject("SELECT contract_no FROM employee_contracts WHERE emp_id = ?", String.class, id);
        assertThat(contractNo).isEqualTo(String.format("CT-2026-%04d-01", id));
        // female employee: gets maternity (F-only types don't track balance) but every balance-tracked type applies
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM leave_balances WHERE emp_id = ?", Integer.class, id)).isPositive();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM audit_log WHERE action = 'EMPLOYEE_CREATED' AND target_id = ?",
                Integer.class, created.get("empCode"))).isEqualTo(1);
    }

    // ---- validation and integrity (7.6, 7.7) -----------------------------------------------------

    @Test
    void invalidEnumValueIs400NotA500() {
        Map<String, Object> body = TestData.employee(uniqueNationalId(), LocalDate.of(2026, 1, 1), TestData.contract(LocalDate.of(2026, 1, 1), null));
        body.put("gender", "X");
        assertThat(post("/api/v1/employees", body, ADMIN).status()).isEqualTo(400);
    }

    @Test
    void omittedOptionalEnumsAreFineAndMalformedNationalIdIs400() {
        Map<String, Object> body = TestData.employee("123", LocalDate.of(2026, 1, 1), TestData.contract(LocalDate.of(2026, 1, 1), null));
        Response response = post("/api/v1/employees", body, ADMIN);
        assertThat(response.status()).isEqualTo(400);
        assertThat(response.body()).contains("nationalId");
    }

    @Test
    void duplicateNationalIdIs409WithAReadableMessage() {
        String nationalId = uniqueNationalId();
        create(nationalId, TestData.contract(LocalDate.of(2026, 1, 1), null));

        Response duplicate = post("/api/v1/employees",
                TestData.employee(nationalId, LocalDate.of(2026, 1, 1), TestData.contract(LocalDate.of(2026, 1, 1), null)), ADMIN);
        assertThat(duplicate.status()).isEqualTo(409);
        assertThat(duplicate.json().get("message")).isEqualTo("An employee with this national ID already exists");
        assertThat(duplicate.body()).doesNotContainIgnoringCase("sql");
    }

    @Test
    void blankOptionalFieldsAreStoredAsNullSoTheyNeverCollideOnUniqueColumns() {
        for (int i = 0; i < 2; i++) {
            Map<String, Object> body = TestData.employee(uniqueNationalId(), LocalDate.of(2026, 1, 1), TestData.contract(LocalDate.of(2026, 1, 1), null));
            body.put("email", "");        // UNIQUE column
            body.put("insuranceNo", "");  // UNIQUE column
            body.put("mobile", "");
            Response response = post("/api/v1/employees", body, ADMIN);
            assertThat(response.status()).as(response.body()).isEqualTo(200);
            assertThat(response.json().get("email")).isNull();
        }
    }

    @Test
    void contractShorterThanTwoMonthsIs400() {
        Map<String, Object> body = TestData.employee(uniqueNationalId(), LocalDate.of(2026, 1, 1),
                TestData.contract(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 20)));
        Response response = post("/api/v1/employees", body, ADMIN);
        assertThat(response.status()).isEqualTo(400);
        assertThat(response.json().get("message").toString()).contains("at least 2 months");
    }

    // ---- optimistic locking (7.8) -----------------------------------------------------------------

    @Test
    void updateWithAStaleVersionIs409() {
        String nationalId = uniqueNationalId();
        Map<String, Object> created = create(nationalId, TestData.contract(LocalDate.of(2026, 1, 1), null));
        long id = ((Number) created.get("id")).longValue();
        int version = ((Number) created.get("version")).intValue();

        Map<String, Object> edit = TestData.employee(nationalId, LocalDate.of(2026, 1, 1), null);
        edit.put("version", version);
        edit.put("mobile", "01000000001");
        Response first = call(HttpMethod.PUT, "/api/v1/employees/" + id, edit, ADMIN);
        assertThat(first.status()).as(first.body()).isEqualTo(200);

        edit.put("mobile", "01000000002"); // second editor still holds the old version
        assertThat(call(HttpMethod.PUT, "/api/v1/employees/" + id, edit, ADMIN).status()).isEqualTo(409);
    }

    // ---- termination (10.2) -----------------------------------------------------------------------

    @Test
    void terminationIsRefusedWhileALoanIsOutstanding() {
        long id = idOf(create(TestData.contract(LocalDate.of(2026, 1, 1), null)));
        Map<String, Object> loan = new HashMap<>(Map.of("empId", id, "type", "ADVANCE", "principalAmount", 3000,
                "installmentsCount", 3, "monthlyInstallment", 1000, "startPeriod", "2026-10", "requestDate", "2026-09-01"));
        assertThat(post("/api/v1/loans", loan, ADMIN).status()).isEqualTo(200);

        Response response = post("/api/v1/employees/" + id + "/terminate", Map.of("terminationDate", "2026-09-30"), ADMIN);
        assertThat(response.status()).isEqualTo(409);
        assertThat(response.json().get("message").toString()).contains("owes 3000");
    }

    @Test
    void terminationSetsTheDateClosesTheContractAndCancelsPendingLeave() {
        long id = idOf(create(TestData.contract(LocalDate.of(2026, 1, 1), LocalDate.of(2027, 12, 31))));
        jdbc.update("""
                INSERT INTO leave_requests (emp_id, type_id, start_date, end_date, days_count, status, applied_on)
                VALUES (?, 1, '2026-11-01', '2026-11-02', 2, 'PENDING', CURDATE())""", id);

        Response response = post("/api/v1/employees/" + id + "/terminate", Map.of("terminationDate", "2026-09-30", "reason", "resigned"), ADMIN);
        assertThat(response.status()).as(response.body()).isEqualTo(200);
        assertThat(response.json().get("empStatus")).isEqualTo("TERMINATED");
        assertThat(response.json().get("terminationDate")).isEqualTo("2026-09-30");

        Map<String, Object> contract = jdbc.queryForMap("SELECT status, end_date, original_end_date FROM employee_contracts WHERE emp_id = ?", id);
        assertThat(contract.get("status")).isEqualTo("TERMINATED");
        assertThat(contract.get("end_date").toString()).isEqualTo("2026-09-30");
        assertThat(contract.get("original_end_date").toString()).isEqualTo("2027-12-31");
        assertThat(jdbc.queryForObject("SELECT status FROM leave_requests WHERE emp_id = ?", String.class, id)).isEqualTo("CANCELLED");

        // second termination is a conflict, not a silent no-op
        assertThat(post("/api/v1/employees/" + id + "/terminate", null, ADMIN).status()).isEqualTo(409);
    }

    // ---- contract renewal (5.4, 10.5) ----------------------------------------------------------------

    @Test
    void renewalEndsTheOldContractTheDayBeforeAndKeepsItsOriginalEndDate() {
        long id = idOf(create(TestData.contract(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31))));

        Response renewed = post("/api/v1/employees/" + id + "/contracts",
                TestData.contract(LocalDate.of(2026, 10, 1), LocalDate.of(2027, 9, 30)), ADMIN);
        assertThat(renewed.status()).as(renewed.body()).isEqualTo(200);
        assertThat(renewed.json().get("contractNo")).isEqualTo(String.format("CT-2026-%04d-02", id));

        Map<String, Object> old = jdbc.queryForMap(
                "SELECT status, end_date, original_end_date FROM employee_contracts WHERE emp_id = ? AND contract_no LIKE '%-01'", id);
        assertThat(old.get("status")).isEqualTo("EXPIRED");
        assertThat(old.get("end_date").toString()).isEqualTo("2026-09-30");
        assertThat(old.get("original_end_date").toString()).isEqualTo("2026-12-31");
    }

    @Test
    void renewalStartingBeforeTheCurrentContractIs400AndOpenEndedContractsCanBeRenewed() {
        long id = idOf(create(TestData.contract(LocalDate.of(2026, 3, 1), null))); // open-ended: used to throw a NullPointerException

        assertThat(post("/api/v1/employees/" + id + "/contracts", TestData.contract(LocalDate.of(2026, 2, 1), null), ADMIN).status())
                .isEqualTo(400);
        assertThat(post("/api/v1/employees/" + id + "/contracts", TestData.contract(LocalDate.of(2026, 6, 1), null), ADMIN).status())
                .isEqualTo(200);
    }

    // ---- salary components (5.8) ----------------------------------------------------------------------

    @Test
    void removingAComponentThatWasInEffectEndDatesItInsteadOfDeleting() {
        long id = idOf(create(TestData.contract(LocalDate.of(2026, 1, 1), null)));
        Response added = post("/api/v1/employees/" + id + "/salary-components",
                Map.of("compId", 3, "amount", 900, "effectiveFrom", "2026-01-01"), ADMIN);
        assertThat(added.status()).as(added.body()).isEqualTo(200);
        long componentId = ((Number) added.json().get("id")).longValue();

        assertThat(call(HttpMethod.DELETE, "/api/v1/employees/" + id + "/salary-components/" + componentId, null, ADMIN).status())
                .isEqualTo(200);
        List<Map<String, Object>> rows = jdbc.queryForList("SELECT effective_to FROM employee_salary_components WHERE id = ?", componentId);
        assertThat(rows).hasSize(1);
        assertThat(rows.getFirst().get("effective_to").toString()).isEqualTo(LocalDate.now().minusDays(1).toString());
    }

    // ---- helpers ----------------------------------------------------------------------------------------

    private Map<String, Object> create(Map<String, Object> contract) {
        return create(uniqueNationalId(), contract);
    }

    private Map<String, Object> create(String nationalId, Map<String, Object> contract) {
        Response response = post("/api/v1/employees", TestData.employee(nationalId, LocalDate.of(2026, 1, 1), contract), ADMIN);
        assertThat(response.status()).as(response.body()).isEqualTo(200);
        return response.json();
    }

    private static long idOf(Map<String, Object> employee) {
        return ((Number) employee.get("id")).longValue();
    }
}
