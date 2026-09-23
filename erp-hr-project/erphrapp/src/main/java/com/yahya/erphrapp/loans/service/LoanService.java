package com.yahya.erphrapp.loans.service;

import com.yahya.erphrapp.audit.AuditService;
import com.yahya.erphrapp.employee.entity.Employee;
import com.yahya.erphrapp.employee.repository.EmployeeRepository;
import com.yahya.erphrapp.exception.BadRequestException;
import com.yahya.erphrapp.exception.ConflictException;
import com.yahya.erphrapp.exception.ResourceNotFoundException;
import com.yahya.erphrapp.loans.dto.LoanInstallmentResponse;
import com.yahya.erphrapp.loans.dto.LoanRequest;
import com.yahya.erphrapp.loans.dto.LoanResponse;
import com.yahya.erphrapp.loans.entity.Loan;
import com.yahya.erphrapp.loans.mapper.LoanInstallmentMapper;
import com.yahya.erphrapp.loans.mapper.LoanMapper;
import com.yahya.erphrapp.loans.repository.LoanInstallmentRepository;
import com.yahya.erphrapp.loans.repository.LoanRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LoanService {

    private final LoanMapper loanMapper;
    private final LoanRepository loanRepository;
    private final EmployeeRepository employeeRepository;
    private final LoanInstallmentMapper loanInstallmentMapper;
    private final LoanInstallmentRepository loanInstallmentRepository;
    private final AuditService auditService;

    public LoanService(LoanInstallmentRepository loanInstallmentRepository, LoanInstallmentMapper loanInstallmentMapper,
                       EmployeeRepository employeeRepository, LoanMapper loanMapper, LoanRepository loanRepository,
                       AuditService auditService) {
        this.loanMapper = loanMapper;
        this.loanInstallmentMapper = loanInstallmentMapper;
        this.loanInstallmentRepository = loanInstallmentRepository;
        this.loanRepository = loanRepository;
        this.employeeRepository = employeeRepository;
        this.auditService = auditService;
    }

    // read all loans
    @Transactional(readOnly = true)
    public Page<LoanResponse> getLoans(Pageable pageable) {
        return loanRepository.findAllBy(pageable).map(loanMapper::toResponse);
    }

    // read all loans for one employee
    @Transactional(readOnly = true)
    public Page<LoanResponse> getLoansByEmployeeId(Long employeeId, Pageable pageable) {
        return loanRepository.findAllByEmployeeId(employeeId, pageable).map(loanMapper::toResponse);
    }

    // read one loan by loan id
    @Transactional(readOnly = true)
    public LoanResponse getLoan(Long id) {
        return loanMapper.toResponse(find(id));
    }

    // create new loan
    @Transactional
    public LoanResponse createLoan(LoanRequest request) {
        Employee employee = employeeRepository.findById(request.getEmpId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmpId()));
        if (!employee.isActive()) {
            throw new ConflictException("Loans can only be granted to active employees");
        }
        if (request.getMonthlyInstallment().compareTo(request.getPrincipalAmount()) > 0) {
            throw new BadRequestException("The monthly installment cannot be larger than the principal");
        }
        BigDecimal coverable = request.getMonthlyInstallment().multiply(BigDecimal.valueOf(request.getInstallmentsCount()));
        if (coverable.compareTo(request.getPrincipalAmount()) < 0) {
            throw new BadRequestException(request.getInstallmentsCount() + " installments of " + request.getMonthlyInstallment()
                    + " don't cover the principal of " + request.getPrincipalAmount());
        }

        Employee approvedBy = null;
        if (request.getApprovedById() != null) {
            approvedBy = employeeRepository.findById(request.getApprovedById())
                    .orElseThrow(() -> new ResourceNotFoundException("Approver", request.getApprovedById()));
        }

        Loan loan = loanMapper.toEntity(request);
        loan.setEmployee(employee);
        loan.setApprovedBy(approvedBy);
        loan.setStatus(Loan.LoanStatus.ACTIVE); // every new loan starts ACTIVE, not client-set
        loan.setRemainingBalance(request.getPrincipalAmount()); // full amount owed at creation
        loanRepository.save(loan);

        auditService.record("LOAN_CREATED", "LOAN", loan.getId(),
                employee.getEmpCode() + " " + loan.getType() + " principal=" + loan.getPrincipalAmount()
                        + " x" + loan.getInstallmentsCount() + " from " + loan.getStartPeriod());
        return loanMapper.toResponse(loan);
    }

    // settle a loan in full
    @Transactional
    public void closeLoan(Long loanId) {
        Loan loan = find(loanId);
        BigDecimal settled = loan.getRemainingBalance();
        loan.close();
        loanRepository.save(loan);
        auditService.record("LOAN_CLOSED", "LOAN", loanId, "settled=" + settled);
    }

    // withdraw a loan
    @Transactional
    public void cancelLoan(Long loanId) {
        Loan loan = find(loanId);
        loan.cancel();
        loanRepository.save(loan);
        auditService.record("LOAN_CANCELLED", "LOAN", loanId, "remaining=" + loan.getRemainingBalance());
    }

    // read all installments for one loan, oldest first
    @Transactional(readOnly = true)
    public List<LoanInstallmentResponse> getInstallmentsForLoan(Long loanId) {
        if (!loanRepository.existsById(loanId)) {
            throw new ResourceNotFoundException("Loan", loanId);
        }
        return loanInstallmentRepository.findAllByLoanIdOrderByPeriodCode(loanId).stream()
                .map(loanInstallmentMapper::toResponse).toList();
    }

    private Loan find(Long id) {
        return loanRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Loan", id));
    }
}
