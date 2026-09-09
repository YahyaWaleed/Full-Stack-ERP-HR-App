package com.yahya.erphrapp.employee.controller;

import com.yahya.erphrapp.employee.dto.EmployeeContractRequest;
import com.yahya.erphrapp.employee.dto.EmployeeContractResponse;
import com.yahya.erphrapp.employee.entity.Employee;
import com.yahya.erphrapp.employee.entity.EmployeeContract;
import com.yahya.erphrapp.employee.service.EmployeeContractService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EmployeeContractController {

    // inject the service
    private final EmployeeContractService employeeContractService;
    public EmployeeContractController(EmployeeContractService employeeContractService) {
        this.employeeContractService = employeeContractService;
    }

    //read contracts
    @GetMapping("/contracts")
    public List<EmployeeContractResponse> getContracts() {
        return employeeContractService.getContracts();
    }

    // list all contracts for one employee
    @GetMapping("/employees/{empId}/contracts")
    public List<EmployeeContractResponse> getContractsForEmployee(@PathVariable Long empId) {
        return employeeContractService.getContractsForEmployee(empId);
    }

    // read one contract
    @GetMapping("/contracts/{id}")
    public EmployeeContractResponse getContract(@PathVariable Long id) {
        return employeeContractService.getContract(id);
    }

    // create new contract
    @PostMapping("/employees/{id}/contracts")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public EmployeeContractResponse createContract(@PathVariable Long id, @Valid @RequestBody EmployeeContractRequest employeeContractRequest) {
        return employeeContractService.createContract(id, employeeContractRequest);
    }

    // end a contract
    @PatchMapping("/contracts/{id}")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public void endContract(@PathVariable Long id) {
        employeeContractService.endContract(id);
    }
}
