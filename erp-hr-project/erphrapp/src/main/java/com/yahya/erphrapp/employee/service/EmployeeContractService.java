package com.yahya.erphrapp.employee.service;

import com.yahya.erphrapp.employee.dto.EmployeeContractRequest;
import com.yahya.erphrapp.employee.dto.EmployeeContractResponse;
import com.yahya.erphrapp.employee.entity.Employee;
import com.yahya.erphrapp.employee.entity.EmployeeContract;
import com.yahya.erphrapp.employee.mapper.EmployeeContractMapper;
import com.yahya.erphrapp.employee.repository.EmployeeContractRepository;
import com.yahya.erphrapp.employee.repository.EmployeeRepository;
import com.yahya.erphrapp.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeContractService {
    // inject repos and mapper
    private final EmployeeContractMapper employeeContractMapper;
    private  final EmployeeContractRepository employeeContractRepository;
    private final EmployeeRepository employeeRepository;

    public EmployeeContractService(EmployeeRepository employeeRepository, EmployeeContractMapper employeeContractMapper, EmployeeContractRepository employeeContractRepository) {
        this.employeeContractMapper = employeeContractMapper;
        this.employeeContractRepository = employeeContractRepository;
        this.employeeRepository = employeeRepository;
    }

    // read one contract
    public EmployeeContractResponse getContract(Long id) {
        EmployeeContract employeeContract = employeeContractRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee Contract", id));
        return employeeContractMapper.toResponse(employeeContract);
    }

    // read all contracts
    public List<EmployeeContractResponse> getContracts() {
        return employeeContractRepository.findAll().stream().map(employeeContractMapper::toResponse).toList();
    }

    // update (renew) contract
    @Transactional
    public EmployeeContractResponse createContract(Long empId, EmployeeContractRequest employeeContractRequest) {
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", empId));

        // Find the current active contract
        EmployeeContract oldContract = employeeContractRepository
                .findByEmployeeIdAndStatus(empId, EmployeeContract.ContractStatus.ACTIVE)
                .orElseThrow(() -> new IllegalStateException(
                        "Employee does not have an active contract to renew"
                ));

        // Validate start date
        if (employeeContractRequest.getStartDate() == null) {
            throw new IllegalArgumentException("Start date is required");
        }

        if (!employeeContractRequest.getStartDate().isAfter(oldContract.getEndDate())) {
            throw new IllegalArgumentException(
                    "New contract start date must be after the current contract end date"
            );
        }

        // Validate end date
        if (employeeContractRequest.getEndDate() != null) {

            if (!employeeContractRequest.getEndDate()
                    .isAfter(employeeContractRequest.getStartDate())) {
                throw new IllegalArgumentException(
                        "End date must be after the start date"
                );
            }

            // Contract must be at least 2 calendar months long
            if (employeeContractRequest.getEndDate()
                    .isBefore(employeeContractRequest.getStartDate().plusMonths(2))) {
                throw new IllegalArgumentException(
                        "Contract must be at least 2 months long"
                );
            }
        }

        // Get the number of existing contracts for this employee
        int renewalNumber = employeeContractRepository.findByEmployeeId(empId).size();

        // Generate contract number
        int contractYear = employeeContractRequest.getStartDate().getYear();

        String contractNo = String.format(
                "CT-%d-%04d-%02d",
                contractYear,
                empId,
                renewalNumber
        );

        // Create the new contract
        EmployeeContract newContract = new EmployeeContract();

        newContract.setEmployee(employee);
        newContract.setContractNo(contractNo);
        newContract.setContractType(
                EmployeeContract.ContractType.valueOf(
                        employeeContractRequest.getContractType()
                )
        );
        newContract.setStartDate(employeeContractRequest.getStartDate());
        newContract.setEndDate(employeeContractRequest.getEndDate());
        newContract.setBasicSalary(employeeContractRequest.getBasicSalary());
        newContract.setCurrency(employeeContractRequest.getCurrency());
        newContract.setWeeklyHours(employeeContractRequest.getWeeklyHours());
        newContract.setAnnualLeaveDays(employeeContractRequest.getAnnualLeaveDays());
        newContract.setProbationMonths(employeeContractRequest.getProbationMonths());
        newContract.setNotes(employeeContractRequest.getNotes());
        newContract.setStatus(EmployeeContract.ContractStatus.ACTIVE);

        // Close the old contract
        oldContract.setStatus(EmployeeContract.ContractStatus.EXPIRED);
        oldContract.setEndDate(
                employeeContractRequest.getStartDate().minusDays(1)
        );

        // Save both contracts
        employeeContractRepository.save(oldContract);
        employeeContractRepository.save(newContract);

        return employeeContractMapper.toResponse(newContract);
    }
    // end a contract
    @Transactional
    public void endContract(Long id) {
        EmployeeContract employeeContract = employeeContractRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee Contract", id));
        employeeContract.setStatus(EmployeeContract.ContractStatus.TERMINATED);
        employeeContractRepository.save(employeeContract);
    }


    public List<EmployeeContractResponse> getContractsForEmployee(Long empId) {
        return employeeContractRepository.findByEmployeeId(empId)
                .stream()
                .map(employeeContractMapper::toResponse)
                .toList();
    }
}
