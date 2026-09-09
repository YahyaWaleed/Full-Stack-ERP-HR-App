package com.yahya.erphrapp.payroll.controller;

import com.yahya.erphrapp.payroll.dto.SalaryComponentResponse;
import com.yahya.erphrapp.payroll.service.SalaryComponentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/salary-components")
public class SalaryComponentController {

    private final SalaryComponentService salaryComponentService;

    public SalaryComponentController(SalaryComponentService salaryComponentService) {
        this.salaryComponentService = salaryComponentService;
    }

    // read the full salary component catalog
    @GetMapping
    public List<SalaryComponentResponse> getComponents() {
        return salaryComponentService.getComponents();
    }

    // read one salary component by its own ID
    @GetMapping("/{id}")
    public SalaryComponentResponse getComponent(@PathVariable Long id) {
        return salaryComponentService.getComponent(id);
    }
}