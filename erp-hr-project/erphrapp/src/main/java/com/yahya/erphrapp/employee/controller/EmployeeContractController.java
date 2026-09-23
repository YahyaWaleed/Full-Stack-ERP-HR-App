package com.yahya.erphrapp.employee.controller;

import com.yahya.erphrapp.employee.dto.EmployeeContractRequest;
import com.yahya.erphrapp.employee.dto.EmployeeContractResponse;
import com.yahya.erphrapp.employee.service.EmployeeContractService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmployeeContractController {

    private final EmployeeContractService employeeContractService;

    public EmployeeContractController(EmployeeContractService employeeContractService) {
        this.employeeContractService = employeeContractService;
    }

    // all contracts, paginated
    @GetMapping("/contracts")
    public Page<EmployeeContractResponse> getContracts(@PageableDefault(size = 25, sort = "startDate") Pageable pageable) {
        return employeeContractService.getContracts(pageable);
    }

    // list all contracts for one employee, newest first
    @GetMapping("/employees/{empId}/contracts")
    public List<EmployeeContractResponse> getContractsForEmployee(@PathVariable Long empId) {
        return employeeContractService.getContractsForEmployee(empId);
    }

    // read one contract
    @GetMapping("/contracts/{id}")
    public EmployeeContractResponse getContract(@PathVariable Long id) {
        return employeeContractService.getContract(id);
    }

    // renew: create the next contract; the current one ends the day before
    @PostMapping("/employees/{empId}/contracts")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public EmployeeContractResponse renewContract(@PathVariable Long empId, @Valid @RequestBody EmployeeContractRequest employeeContractRequest) {
        return employeeContractService.renewContract(empId, employeeContractRequest);
    }

    // end a contract early: POST /contracts/{id}/end?endDate=2026-09-30 (defaults to today)
    @PostMapping("/contracts/{id}/end")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public void endContract(@PathVariable Long id, @RequestParam(required = false) LocalDate endDate) {
        employeeContractService.endContract(id, endDate);
    }
}
