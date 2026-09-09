package com.yahya.erphrapp.employee.controller;

import com.yahya.erphrapp.employee.dto.EmployeeSalaryComponentRequest;
import com.yahya.erphrapp.employee.dto.EmployeeSalaryComponentResponse;
import com.yahya.erphrapp.employee.service.EmployeeSalaryComponentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EmployeeSalaryComponentController {

    private final EmployeeSalaryComponentService employeeSalaryComponentService;

    public EmployeeSalaryComponentController(EmployeeSalaryComponentService employeeSalaryComponentService) {
        this.employeeSalaryComponentService = employeeSalaryComponentService;
    }

    // read salary components for one employee
    @GetMapping("/employees/{empId}/salary-components")
    public List<EmployeeSalaryComponentResponse> getComponentsForEmployee(@PathVariable Long empId) {
        return employeeSalaryComponentService.getComponentsForEmployee(empId);
    }

    // create a salary component for an employee
    @PostMapping("/employees/{empId}/salary-components")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public EmployeeSalaryComponentResponse createComponent(@PathVariable Long empId,
                                                           @Valid @RequestBody EmployeeSalaryComponentRequest request) {
        return employeeSalaryComponentService.createComponent(empId, request);
    }

    // update a salary component
    @PatchMapping("/salary-components/{id}")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public EmployeeSalaryComponentResponse updateComponent(@PathVariable Long id,
                                                           @Valid @RequestBody EmployeeSalaryComponentRequest request) {
        return employeeSalaryComponentService.updateComponent(id, request);
    }

    // delete a salary component
    @DeleteMapping("/salary-components/{id}")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public void deleteComponent(@PathVariable Long id) {
        employeeSalaryComponentService.deleteComponent(id);
    }
}