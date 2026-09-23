package com.yahya.erphrapp.loans.controller;

import com.yahya.erphrapp.loans.dto.LoanInstallmentResponse;
import com.yahya.erphrapp.loans.dto.LoanRequest;
import com.yahya.erphrapp.loans.dto.LoanResponse;
import com.yahya.erphrapp.loans.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping("/loans")
    public Page<LoanResponse> getLoans(@PageableDefault(size = 25, sort = "requestDate") Pageable pageable) {
        return loanService.getLoans(pageable);
    }

    @GetMapping("/loans/{loanId}")
    public LoanResponse getLoan(@PathVariable Long loanId) {
        return loanService.getLoan(loanId);
    }

    @PostMapping("/loans")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public LoanResponse createLoan(@RequestBody @Valid LoanRequest loanRequest) {
        return loanService.createLoan(loanRequest);
    }

    @GetMapping("/employees/{employeeId}/loans")
    public Page<LoanResponse> getLoansByEmployeeId(@PathVariable Long employeeId,
                                                   @PageableDefault(size = 25, sort = "requestDate") Pageable pageable) {
        return loanService.getLoansByEmployeeId(employeeId, pageable);
    }

    // state transitions are POST /{id}/{action}, the same as leave requests
    @PostMapping("/loans/{loanId}/close")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public void closeLoan(@PathVariable Long loanId) {
        loanService.closeLoan(loanId);
    }

    @PostMapping("/loans/{loanId}/cancel")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public void cancelLoan(@PathVariable Long loanId) {
        loanService.cancelLoan(loanId);
    }

    @GetMapping("/loans/{loanId}/installments")
    public List<LoanInstallmentResponse> getInstallmentsForLoan(@PathVariable Long loanId) {
        return loanService.getInstallmentsForLoan(loanId);
    }
}
