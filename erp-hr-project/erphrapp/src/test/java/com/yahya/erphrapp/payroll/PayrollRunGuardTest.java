package com.yahya.erphrapp.payroll;

import com.yahya.erphrapp.AbstractIntegrationTest;
import com.yahya.erphrapp.exception.ConflictException;
import com.yahya.erphrapp.payroll.service.PayrollPeriodService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PayrollRunGuardTest extends AbstractIntegrationTest {

    @Autowired
    private PayrollPeriodService payrollPeriodService;

    @Test
    void runningPayrollOnNonOpenPeriodThrowsConflict() {
        // replace with a real periodCode from your seed data whose status
        // is already PROCESSED, PAID, or CLOSED — not OPEN
        String alreadyProcessedPeriodCode = "2026-05";

        assertThrows(ConflictException.class, () ->
                payrollPeriodService.runPayroll(alreadyProcessedPeriodCode));
    }
}