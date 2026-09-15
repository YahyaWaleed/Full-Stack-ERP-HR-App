package com.yahya.erphrapp.leaves;

import com.yahya.erphrapp.AbstractIntegrationTest;
import com.yahya.erphrapp.exception.ConflictException;
import com.yahya.erphrapp.leaves.entity.LeaveBalance;
import com.yahya.erphrapp.leaves.entity.LeaveRequest;
import com.yahya.erphrapp.leaves.repository.LeaveBalanceRepository;
import com.yahya.erphrapp.leaves.repository.LeaveRequestRepository;
import com.yahya.erphrapp.leaves.service.LeaveRequestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

class LeaveApprovalTest extends AbstractIntegrationTest {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Test
    void approvingWithInsufficientBalanceThrowsConflict() {
        // request #6: employee 24, leave type 1 (Annual), 4 days, in fiscal year 2026
        LeaveRequest request = leaveRequestRepository.findById(6L).orElseThrow();

        // deliberately shrink this employee's real balance below what the request needs,
        // so the test doesn't depend on guessing seed data numbers that could change
        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndFiscalYear(
                        request.getEmployee().getId(),
                        request.getLeaveType().getId(),
                        2026)
                .orElseThrow();

        balance.setEntitledDays(BigDecimal.ZERO);
        balance.setUsedDays(BigDecimal.ZERO);
        leaveBalanceRepository.save(balance);

        assertThrows(ConflictException.class, () ->
                leaveRequestService.approveRequest(request.getId()));
    }
}