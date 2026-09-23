package com.yahya.erphrapp.payroll.controller;

import com.yahya.erphrapp.payroll.dto.PayrollPaymentsResponse;
import com.yahya.erphrapp.payroll.service.PayrollPaymentsService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@PreAuthorize("hasRole('HR_ADMIN')")
@RequestMapping("/api/v1")
@RestController
public class PayrollPaymentsController {

    // inject the service
    private final PayrollPaymentsService payrollPaymentsService;

    public PayrollPaymentsController(PayrollPaymentsService payrollPaymentsService) {
        this.payrollPaymentsService = payrollPaymentsService;
    }

    // read one payment by its payment id
    @GetMapping("/payroll-payments/{id}")
    public PayrollPaymentsResponse getPayment(@PathVariable Long id) {
        return payrollPaymentsService.getPayment(id);
    }

    // read all payments made in a specific period
    @GetMapping("/payroll-periods/{periodCode}/payments")
    public List<PayrollPaymentsResponse> getPaymentsByPeriodCode(@PathVariable String periodCode) {
        return payrollPaymentsService.getPaymentsForPeriod(periodCode);
    }

    // read payment for a specific payslip
    @GetMapping("/payslips/{payslipId}/payment")
    public PayrollPaymentsResponse getPaymentForPayslip(@PathVariable Long payslipId) {
        return payrollPaymentsService.getPaymentForPayslip(payslipId);
    }
}
