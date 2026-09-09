package com.yahya.erphrapp.payroll.controller;

import com.yahya.erphrapp.payroll.dto.PayrollPeriodRequest;
import com.yahya.erphrapp.payroll.dto.PayrollPeriodResponse;
import com.yahya.erphrapp.payroll.entity.PayrollPeriod;
import com.yahya.erphrapp.payroll.service.PayrollPeriodService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PayrollPeriodController {
    // inject the service
    private final PayrollPeriodService payrollPeriodService;

    public PayrollPeriodController(PayrollPeriodService payrollPeriodService) {
        this.payrollPeriodService = payrollPeriodService;
    }

    // read all periods
    @GetMapping("/payroll-periods")
    public List<PayrollPeriodResponse> getPeriods() {
        return payrollPeriodService.getPeriods();
    }

    // read one period by its period code
    @GetMapping("/payroll-periods/{periodCode}")
    public PayrollPeriodResponse getPeriod(@PathVariable String periodCode) {
        return payrollPeriodService.getPeriod(periodCode);
    }

    // read all periods in the same fiscal year
    @GetMapping("/payroll-periods/fiscal-year/{year}")
    public List<PayrollPeriodResponse> getPeriodsByFiscalYear(@PathVariable int year) {
        return payrollPeriodService.getPeriodsInFiscalYear(year);
    }

    // create a new period
    @PostMapping("/payroll-periods")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public PayrollPeriodResponse createPeriod(@RequestBody PayrollPeriodRequest payrollPeriodRequest) {
        return payrollPeriodService.createPeriod(payrollPeriodRequest);
    }

    // run the payroll for a period
    @PostMapping("/payroll-periods/{periodCode}/run")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public void runPayroll(@PathVariable String periodCode) {
         payrollPeriodService.runPayroll(periodCode);
    }

    // pay the period
    @PostMapping("/payroll-periods/{periodCode}/pay")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public void payPeriod(@PathVariable String periodCode, @RequestParam String method) {
        payrollPeriodService.payPeriod(periodCode, method);
    }
}
