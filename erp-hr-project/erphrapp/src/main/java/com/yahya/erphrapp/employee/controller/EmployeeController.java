package com.yahya.erphrapp.employee.controller;

import com.yahya.erphrapp.employee.dto.EmployeeRequest;
import com.yahya.erphrapp.employee.dto.EmployeeResponse;
import com.yahya.erphrapp.employee.dto.TerminateEmployeeRequest;
import com.yahya.erphrapp.employee.entity.Employee;
import com.yahya.erphrapp.employee.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // create employee with their first contract
    @PostMapping
    @PreAuthorize("hasRole('HR_ADMIN')")
    public EmployeeResponse createEmployee(@Valid @RequestBody EmployeeRequest employeeRequest) {
        return employeeService.createEmployee(employeeRequest);
    }

    // one endpoint for every employee list: GET /employees?branchId=&deptId=&jobId=&status=&managerial=&q=&page=&size=&sort=
    @GetMapping
    public Page<EmployeeResponse> getEmployees(@RequestParam(required = false) Long branchId,
                                               @RequestParam(required = false) Long deptId,
                                               @RequestParam(required = false) Long jobId,
                                               @RequestParam(required = false) Employee.EmployeeStatus status,
                                               @RequestParam(required = false) Boolean managerial,
                                               @RequestParam(required = false) String q,
                                               @PageableDefault(size = 25, sort = "empCode") Pageable pageable) {
        return employeeService.getEmployees(branchId, deptId, jobId, status, managerial, q, pageable);
    }

    // read one employee
    @GetMapping("/{id}")
    public EmployeeResponse getEmployee(@PathVariable Long id) {
        return employeeService.getEmployee(id);
    }

    // update employee
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public EmployeeResponse updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeRequest employeeRequest) {
        return employeeService.updateEmployee(id, employeeRequest);
    }

    // terminate employee (body optional: { terminationDate, reason })
    @PostMapping("/{id}/terminate")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public EmployeeResponse terminateEmployee(@PathVariable Long id,
                                              @Valid @RequestBody(required = false) TerminateEmployeeRequest request) {
        return employeeService.terminateEmployee(id, request);
    }
}
