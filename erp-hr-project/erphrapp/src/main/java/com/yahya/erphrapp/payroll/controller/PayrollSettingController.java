package com.yahya.erphrapp.payroll.controller;

import com.yahya.erphrapp.payroll.dto.PayrollSettingResponse;
import com.yahya.erphrapp.payroll.service.PayrollSettingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payroll-settings")
public class PayrollSettingController {

    private final PayrollSettingService payrollSettingService;

    public PayrollSettingController(PayrollSettingService payrollSettingService) {
        this.payrollSettingService = payrollSettingService;
    }

    // read all payroll settings
    @GetMapping
    public List<PayrollSettingResponse> getSettings() {
        return payrollSettingService.getSettings();
    }

    // read the settings for one fiscal year
    @GetMapping("/{fiscalYear}")
    public PayrollSettingResponse getSetting(@PathVariable int fiscalYear) {
        return payrollSettingService.getSetting(fiscalYear);
    }
}