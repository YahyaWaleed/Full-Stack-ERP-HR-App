package com.yahya.erphrapp.employee.service;

import com.yahya.erphrapp.audit.AuditService;
import com.yahya.erphrapp.employee.dto.EmployeeContractRequest;
import com.yahya.erphrapp.employee.dto.EmployeeContractResponse;
import com.yahya.erphrapp.employee.entity.Employee;
import com.yahya.erphrapp.employee.entity.EmployeeContract;
import com.yahya.erphrapp.employee.mapper.EmployeeContractMapper;
import com.yahya.erphrapp.employee.repository.EmployeeContractRepository;
import com.yahya.erphrapp.employee.repository.EmployeeRepository;
import com.yahya.erphrapp.exception.BadRequestException;
import com.yahya.erphrapp.exception.ConflictException;
import com.yahya.erphrapp.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeContractService {

    private static final int MIN_CONTRACT_MONTHS = 2;

    private final EmployeeContractMapper employeeContractMapper;
    private final EmployeeContractRepository employeeContractRepository;
    private final EmployeeRepository employeeRepository;
    private final ContractNumberGenerator contractNumberGenerator;
    private final AuditService auditService;

    public EmployeeContractService(EmployeeRepository employeeRepository, EmployeeContractMapper employeeContractMapper,
                                   EmployeeContractRepository employeeContractRepository,
                                   ContractNumberGenerator contractNumberGenerator, AuditService auditService) {
        this.employeeContractMapper = employeeContractMapper;
        this.employeeContractRepository = employeeContractRepository;
        this.employeeRepository = employeeRepository;
        this.contractNumberGenerator = contractNumberGenerator;
        this.auditService = auditService;
    }

    // read one contract
    @Transactional(readOnly = true)
    public EmployeeContractResponse getContract(Long id) {
        EmployeeContract employeeContract = employeeContractRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee Contract", id));
        return employeeContractMapper.toResponse(employeeContract);
    }

    // read all contracts, one page at a time
    @Transactional(readOnly = true)
    public Page<EmployeeContractResponse> getContracts(Pageable pageable) {
        return employeeContractRepository.findAllBy(pageable).map(employeeContractMapper::toResponse);
    }

    // an employee's contracts, newest first
    @Transactional(readOnly = true)
    public List<EmployeeContractResponse> getContractsForEmployee(Long empId) {
        return employeeContractRepository.findByEmployeeIdOrderByStartDateDesc(empId)
                .stream()
                .map(employeeContractMapper::toResponse)
                .toList();
    }

    // the first contract, created together with the employee (caller holds the transaction)
    @Transactional
    public EmployeeContract createInitial(Employee employee, EmployeeContractRequest request) {
        validateDates(request);
        EmployeeContract contract = employeeContractMapper.toEntity(request);
        contract.setEmployee(employee);
        contract.setStatus(EmployeeContract.ContractStatus.ACTIVE);
        contract.setContractNo(contractNumberGenerator.next(employee.getId(), request.getStartDate().getYear()));
        return employeeContractRepository.save(contract);
    }

    // renew: the current contract ends the day before the new one starts; its original end date is kept
    @Transactional
    public EmployeeContractResponse renewContract(Long empId, EmployeeContractRequest request) {
        // lock the employee so two renewals can't run at once (contract number + "one active contract")
        Employee employee = employeeRepository.findByIdForUpdate(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", empId));
        if (employee.isTerminated()) {
            throw new ConflictException("Cannot renew the contract of a terminated employee");
        }

        EmployeeContract current = employeeContractRepository
                .findByEmployeeIdAndStatus(empId, EmployeeContract.ContractStatus.ACTIVE)
                .orElseThrow(() -> new ConflictException("Employee does not have an active contract to renew"));

        validateDates(request);
        if (!request.getStartDate().isAfter(current.getStartDate())) {
            throw new BadRequestException("The new contract must start after the current one started (" + current.getStartDate() + ")");
        }

        current.closeOn(request.getStartDate().minusDays(1), EmployeeContract.ContractStatus.EXPIRED);

        EmployeeContract renewed = employeeContractMapper.toEntity(request);
        renewed.setEmployee(employee);
        renewed.setStatus(EmployeeContract.ContractStatus.ACTIVE);
        renewed.setContractNo(contractNumberGenerator.next(empId, request.getStartDate().getYear()));

        employeeContractRepository.save(current);
        employeeContractRepository.save(renewed);

        auditService.record("CONTRACT_RENEWED", "EMPLOYEE", employee.getEmpCode(),
                "old=" + current.getContractNo() + " new=" + renewed.getContractNo()
                        + " start=" + renewed.getStartDate() + " basic=" + renewed.getBasicSalary());
        return employeeContractMapper.toResponse(renewed);
    }

    // end a contract early (defaults to today)
    @Transactional
    public void endContract(Long id, LocalDate endDate) {
        EmployeeContract contract = employeeContractRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee Contract", id));
        LocalDate lastDay = endDate != null ? endDate : LocalDate.now();
        contract.closeOn(lastDay, EmployeeContract.ContractStatus.TERMINATED);
        employeeContractRepository.save(contract);
        auditService.record("CONTRACT_ENDED", "CONTRACT", contract.getContractNo(), "lastDay=" + lastDay);
    }

    // closes the active contract on the termination date, if the employee has one
    @Transactional
    public Optional<EmployeeContract> closeActiveOnTermination(Long empId, LocalDate terminationDate) {
        Optional<EmployeeContract> active = employeeContractRepository.findByEmployeeIdAndStatus(empId, EmployeeContract.ContractStatus.ACTIVE);
        active.ifPresent(c -> {
            // a contract that hasn't started yet simply never starts
            c.closeOn(terminationDate.isBefore(c.getStartDate()) ? c.getStartDate() : terminationDate,
                    EmployeeContract.ContractStatus.TERMINATED);
            employeeContractRepository.save(c);
        });
        return active;
    }

    private static void validateDates(EmployeeContractRequest request) {
        LocalDate start = request.getStartDate();
        LocalDate end = request.getEndDate();
        if (end == null) {
            if (request.getContractType() == EmployeeContract.ContractType.FIXED_TERM) {
                throw new BadRequestException("A fixed-term contract needs an end date");
            }
            return;
        }
        if (!end.isAfter(start)) {
            throw new BadRequestException("End date must be after the start date");
        }
        if (end.isBefore(start.plusMonths(MIN_CONTRACT_MONTHS))) {
            throw new BadRequestException("Contract must be at least " + MIN_CONTRACT_MONTHS + " months long");
        }
    }
}
