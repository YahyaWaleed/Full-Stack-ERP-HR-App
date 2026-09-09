package com.yahya.erphrapp.loans.service;

import com.yahya.erphrapp.employee.entity.Employee;
import com.yahya.erphrapp.employee.repository.EmployeeRepository;
import com.yahya.erphrapp.exception.ResourceNotFoundException;
import com.yahya.erphrapp.loans.dto.LoanInstallmentResponse;
import com.yahya.erphrapp.loans.dto.LoanRequest;
import com.yahya.erphrapp.loans.dto.LoanResponse;
import com.yahya.erphrapp.loans.entity.Loan;
import com.yahya.erphrapp.loans.entity.LoanInstallment;
import com.yahya.erphrapp.loans.mapper.LoanInstallmentMapper;
import com.yahya.erphrapp.loans.mapper.LoanMapper;
import com.yahya.erphrapp.loans.repository.LoanInstallmentRepository;
import com.yahya.erphrapp.loans.repository.LoanRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LoanService {
    // inject the mapper and repo
    private final LoanMapper loanMapper;
    private final LoanRepository loanRepository;
    private final EmployeeRepository employeeRepository;
    private final LoanInstallmentMapper loanInstallmentMapper;
    private final LoanInstallmentRepository loanInstallmentRepository;

    public LoanService(LoanInstallmentRepository loanInstallmentRepository,LoanInstallmentMapper loanInstallmentMapper, EmployeeRepository employeeRepository, LoanMapper loanMapper, LoanRepository loanRepository) {
        this.loanMapper = loanMapper;
        this.loanInstallmentMapper = loanInstallmentMapper;
        this.loanInstallmentRepository = loanInstallmentRepository;
        this.loanRepository = loanRepository;
        this.employeeRepository = employeeRepository;
    }

    // read all loans
    public List<LoanResponse> getLoans() {
        List<Loan> loans = loanRepository.findAll();
        return loans.stream().map(loanMapper::toResponse).toList();
    }

    // read one loan by loan id
    public LoanResponse getLoan(Long id) {
        Loan loan = loanRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Loan", id));
        return loanMapper.toResponse(loan);
    }

    // create new loan
    @Transactional
    public LoanResponse createLoan(LoanRequest request) {
        Employee employee = employeeRepository.findById(request.getEmpId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmpId()));

        Employee approvedBy = null;
        if (request.getApprovedById() != null) {
            approvedBy = employeeRepository.findById(request.getApprovedById())
                    .orElseThrow(() -> new ResourceNotFoundException("Approver", request.getApprovedById()));
        }

        Loan loan = loanMapper.toEntity(request);
        loan.setEmployee(employee);
        loan.setApprovedBy(approvedBy);
        loan.setType(Loan.LoanType.valueOf(request.getType()));
        loan.setStatus(Loan.LoanStatus.ACTIVE); // every new loan starts ACTIVE, not client-set
        loan.setRemainingBalance(request.getPrincipalAmount()); // full amount owed at creation

        loanRepository.save(loan);
        return loanMapper.toResponse(loan);
    }

    // read loans for one employee
    public List<LoanResponse> getLoansByEmployeeId(Long employeeId) {
        List<Loan> loans = loanRepository.findAllByEmployeeId(employeeId);
        return loans.stream().map(loanMapper::toResponse).toList();
    }

    // closing a loan
    @Transactional
    public void closeLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new ResourceNotFoundException("Loan", loanId));
        loan.setStatus(Loan.LoanStatus.CLOSED);
        loan.setRemainingBalance(BigDecimal.ZERO);

        loanRepository.save(loan);
    }

    // cancelling a loan
    @Transactional
    public void cancelLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new ResourceNotFoundException("Loan" , loanId));
        loan.setStatus(Loan.LoanStatus.CANCELLED);

        loanRepository.save(loan);
    }

    // read all installments for one loan
    public List<LoanInstallmentResponse> getInstallmentsForLoan(Long loanId) {
        if (!loanRepository.existsById(loanId)) {
            throw new ResourceNotFoundException("Loan", loanId);
        }
        List<LoanInstallment> installments = loanInstallmentRepository.findAllByLoanId(loanId);
        return installments.stream().map(loanInstallmentMapper::toResponse).toList();
    }
}
