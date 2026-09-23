package com.yahya.erphrapp.employee.controller;

import com.yahya.erphrapp.employee.dto.EmployeeSalaryComponentRequest;
import com.yahya.erphrapp.employee.dto.EmployeeSalaryComponentResponse;
import com.yahya.erphrapp.employee.service.EmployeeSalaryComponentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// an employee's own salary components, nested under the employee
// (/salary-components/{id} is the company-wide component catalog)
@RestController
@RequestMapping("/api/v1/employees/{empId}/salary-components")
public class EmployeeSalaryComponentController {

    private final EmployeeSalaryComponentService employeeSalaryComponentService;

    public EmployeeSalaryComponentController(EmployeeSalaryComponentService employeeSalaryComponentService) {
        this.employeeSalaryComponentService = employeeSalaryComponentService;
    }

    @GetMapping
    public List<EmployeeSalaryComponentResponse> getComponentsForEmployee(@PathVariable Long empId) {
        return employeeSalaryComponentService.getComponentsForEmployee(empId);
    }

    @PostMapping
    @PreAuthorize("hasRole('HR_ADMIN')")
    public EmployeeSalaryComponentResponse createComponent(@PathVariable Long empId,
                                                           @Valid @RequestBody EmployeeSalaryComponentRequest request) {
        return employeeSalaryComponentService.createComponent(empId, request);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public EmployeeSalaryComponentResponse updateComponent(@PathVariable Long empId, @PathVariable Long id,
                                                           @Valid @RequestBody EmployeeSalaryComponentRequest request) {
        return employeeSalaryComponentService.updateComponent(empId, id, request);
    }

    // ends the component (or deletes it if it never took effect)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public void deleteComponent(@PathVariable Long empId, @PathVariable Long id) {
        employeeSalaryComponentService.deleteComponent(empId, id);
    }
}
