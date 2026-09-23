package com.yahya.erphrapp.payroll.controller;

import com.yahya.erphrapp.employee.entity.Employee;
import com.yahya.erphrapp.payroll.dto.PayrollPeriodRequest;
import com.yahya.erphrapp.payroll.dto.PayrollPeriodResponse;
import com.yahya.erphrapp.payroll.dto.PayslipResponse;
import com.yahya.erphrapp.payroll.service.PayrollPeriodService;
import com.yahya.erphrapp.payroll.service.PayslipService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payroll-periods")
public class PayrollPeriodController {

    private final PayrollPeriodService payrollPeriodService;
    private final PayslipService payslipService;

    public PayrollPeriodController(PayrollPeriodService payrollPeriodService, PayslipService payslipService) {
        this.payrollPeriodService = payrollPeriodService;
        this.payslipService = payslipService;
    }

    // all periods, or one fiscal year: GET /payroll-periods?fiscalYear=2026
    @GetMapping
    public List<PayrollPeriodResponse> getPeriods(@RequestParam(required = false) Integer fiscalYear) {
        return payrollPeriodService.getPeriods(fiscalYear);
    }

    @GetMapping("/{periodCode}")
    public PayrollPeriodResponse getPeriod(@PathVariable String periodCode) {
        return payrollPeriodService.getPeriod(periodCode);
    }

    // the payslips of one period
    @GetMapping("/{periodCode}/payslips")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public Page<PayslipResponse> getPayslips(@PathVariable String periodCode,
                                             @PageableDefault(size = 25, sort = "employee.empCode") Pageable pageable) {
        return payslipService.getPayslipsByPeriodCode(periodCode, pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('HR_ADMIN')")
    public PayrollPeriodResponse createPeriod(@Valid @RequestBody PayrollPeriodRequest payrollPeriodRequest) {
        return payrollPeriodService.createPeriod(payrollPeriodRequest);
    }

    @PostMapping("/{periodCode}/run")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public void runPayroll(@PathVariable String periodCode) {
        payrollPeriodService.runPayroll(periodCode);
    }

    // method = BANK, CASH or CHEQUE
    @PostMapping("/{periodCode}/pay")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public void payPeriod(@PathVariable String periodCode, @RequestParam Employee.PaymentMethod method) {
        payrollPeriodService.payPeriod(periodCode, method);
    }
}
