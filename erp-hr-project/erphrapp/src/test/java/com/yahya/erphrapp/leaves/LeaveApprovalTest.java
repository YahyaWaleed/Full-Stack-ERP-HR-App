package com.yahya.erphrapp.leaves;

import com.yahya.erphrapp.AbstractIntegrationTest;
import com.yahya.erphrapp.exception.ConflictException;
import com.yahya.erphrapp.leaves.service.LeaveRequestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class LeaveApprovalTest extends AbstractIntegrationTest {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void approvingWithInsufficientBalanceThrowsConflict() {
        // any pending request on a balance-tracked leave type; empty that balance first
        Long requestId = jdbc.queryForObject("""
                SELECT r.request_id FROM leave_requests r JOIN leave_types t ON t.type_id = r.type_id
                 WHERE r.status = 'PENDING' AND t.affects_balance = 1 ORDER BY r.request_id LIMIT 1""", Long.class);
        jdbc.update("""
                UPDATE leave_balances b JOIN leave_requests r
                    ON r.emp_id = b.emp_id AND r.type_id = b.type_id AND b.fiscal_year = YEAR(r.start_date)
                   SET b.entitled_days = 0, b.used_days = 0, b.carried_forward = 0
                 WHERE r.request_id = ?""", requestId);

        assertThatThrownBy(() -> leaveRequestService.approveRequest(requestId))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("enough remaining leave balance");
        TestTransaction.flagForRollback();
    }
}
