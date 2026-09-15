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
        String alreadyProcessedPeriodCode = "2026-08";

        assertThrows(ConflictException.class, () ->
                payrollPeriodService.runPayroll(alreadyProcessedPeriodCode));
    }
}