package com.yahya.erphrapp.employee.controller;

import com.yahya.erphrapp.employee.dto.EmployeeContractRequest;
import com.yahya.erphrapp.employee.dto.EmployeeRequest;
import com.yahya.erphrapp.employee.dto.EmployeeResponse;
import com.yahya.erphrapp.employee.service.EmployeeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    // inject service
    private final EmployeeService employeeService;
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // create employee with his contract
    @PostMapping
    @PreAuthorize("hasRole('HR_ADMIN')") // check that it is admin not normal user
    public EmployeeResponse createEmployee(@Valid @RequestBody EmployeeRequest employeeRequest) {
        return employeeService.createEmployee(employeeRequest,employeeRequest.getContract());
    }

    // read all employees
    @GetMapping
    public List<EmployeeResponse> getEmployees() {
        return employeeService.getEmployees();
    }

    // read one employee
    @GetMapping("/{id}")
    public EmployeeResponse getEmployee(@PathVariable Long id) {
        return employeeService.getEmployee(id);
    }

    // update employee
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public EmployeeResponse updateEmployee(@PathVariable Long id,@Valid @RequestBody EmployeeRequest employeeRequest) {
        return employeeService.updateEmployee(id, employeeRequest);
    }

    // terminate employee
    @PostMapping("/{id}/terminate")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public void terminateEmployee(@PathVariable Long id) {
        employeeService.terminateEmployee(id);
    }

    // get all employees in one branch
    @GetMapping("/branch/{branchId}")
    public List<EmployeeResponse> getEmployeesByBranchId(@PathVariable Long branchId) {
        return employeeService.getEmployeesByBranchId(branchId);
    }

    // get all employees in one department
    @GetMapping("/department/{deptId}")
    public List<EmployeeResponse> getEmployeesByDeptId(@PathVariable Long deptId) {
        return employeeService.getEmployeesByDeptId(deptId);
    }

    // get all employees with same job title
    @GetMapping("/job/{jobTitleId}")
    public List<EmployeeResponse> getEmployeesByJobTitleId(@PathVariable Long jobTitleId) {
        return employeeService.getEmployeesByJobTitleId(jobTitleId);
    }
}


