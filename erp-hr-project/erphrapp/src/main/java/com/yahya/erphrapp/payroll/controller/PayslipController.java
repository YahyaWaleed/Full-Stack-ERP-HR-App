package com.yahya.erphrapp.payroll.controller;

import com.yahya.erphrapp.payroll.dto.PayslipLineResponse;
import com.yahya.erphrapp.payroll.dto.PayslipResponse;
import com.yahya.erphrapp.payroll.service.PayslipService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// a period's payslips are at /payroll-periods/{periodCode}/payslips
@PreAuthorize("hasRole('HR_ADMIN')")
@RestController
@RequestMapping("/api/v1")
public class PayslipController {

    private final PayslipService payslipService;

    public PayslipController(PayslipService payslipService) {
        this.payslipService = payslipService;
    }

    @GetMapping("/payslips/{id}")
    public PayslipResponse getPayslip(@PathVariable Long id) {
        return payslipService.getPayslip(id);
    }

    @GetMapping("/payslips/{id}/lines")
    public List<PayslipLineResponse> getPayslipLines(@PathVariable Long id) {
        return payslipService.getPayslipLines(id);
    }

    @GetMapping("/employees/{empId}/payslips")
    public List<PayslipResponse> getPayslipsByEmployeeId(@PathVariable Long empId) {
        return payslipService.getPayslipsByEmployeeId(empId);
    }
}
