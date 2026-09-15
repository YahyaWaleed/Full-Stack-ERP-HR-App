package com.yahya.erphrapp.leaves;

import com.yahya.erphrapp.AbstractIntegrationTest;
import com.yahya.erphrapp.exception.ConflictException;
import com.yahya.erphrapp.leaves.service.LeaveRequestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertThrows;

class LeaveApprovalTest extends AbstractIntegrationTest {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Test
    void approvingWithInsufficientBalanceThrowsConflict() {
        // replace with a real leave request ID from your seeded data whose
        // requested days exceed the employee's remaining balance
        Long requestIdExceedingBalance = 999L;

        assertThrows(ConflictException.class, () ->
                leaveRequestService.approveRequest(requestIdExceedingBalance));
    }
}