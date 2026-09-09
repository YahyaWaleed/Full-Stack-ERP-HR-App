package com.yahya.erphrapp.payroll.controller;

import com.yahya.erphrapp.payroll.dto.PayslipLineResponse;
import com.yahya.erphrapp.payroll.dto.PayslipResponse;
import com.yahya.erphrapp.payroll.service.PayslipService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payslips")
public class PayslipController {

    // inject the service
    private final PayslipService payslipService;

    public PayslipController(PayslipService payslipService) {
        this.payslipService = payslipService;
    }

    // read one slip by its id
    @GetMapping("/{id}")
    public PayslipResponse getPayslip(@PathVariable Long id) {
        return payslipService.getPayslip(id);
    }

    //read all payslips for a specific month by periodCode
    @GetMapping("/month/{periodCode}")
    public List<PayslipResponse> getPayslipsByPeriodCode(@PathVariable String periodCode) {
        return payslipService.getPayslipByPeriodCode(periodCode);
    }

    // read one payslip as lines
    @GetMapping("/{id}/lines")
    public List<PayslipLineResponse> getPayslipLines(@PathVariable Long id) {
        return payslipService.getPayslipLines(id);
    }

    // get all payslips for one employee by employeeID
    @GetMapping("/employee/{empId}")
    public List<PayslipResponse> getPayslipsByEmployeeId(@PathVariable Long empId) {
        return payslipService.getPayslipsByEmployeeId(empId);
    }
}
