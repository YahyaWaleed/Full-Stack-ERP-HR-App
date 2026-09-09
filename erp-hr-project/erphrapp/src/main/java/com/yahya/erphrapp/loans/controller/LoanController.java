package com.yahya.erphrapp.loans.controller;

import com.yahya.erphrapp.loans.dto.LoanInstallmentResponse;
import com.yahya.erphrapp.loans.dto.LoanRequest;
import com.yahya.erphrapp.loans.dto.LoanResponse;
import com.yahya.erphrapp.loans.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LoanController {

    private final LoanService loanService;
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // read all loans
    @GetMapping("/loans")
    public List<LoanResponse> getLoans() {
        return  loanService.getLoans();
    }

    // read one loan
    @GetMapping("/loans/{loanId}")
    public LoanResponse getLoan(@PathVariable Long loanId) {
        return loanService.getLoan(loanId);
    }

    // create new loan
    @PostMapping("/loans")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public LoanResponse createLoan(@RequestBody @Valid LoanRequest loanRequest) {
        return loanService.createLoan(loanRequest);
    }

    // read loans for one employee
    @GetMapping("/employees/{employeeId}/loans")
    public List<LoanResponse> getLoansByEmployeeId(@PathVariable Long employeeId) {
        return loanService.getLoansByEmployeeId(employeeId);
    }

    // close loan
    @PatchMapping("/loans/{loanId}/close")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public void closeLoan(@PathVariable Long loanId) {
        loanService.closeLoan(loanId);
    }

    // cancel loan
    @PatchMapping("/loans/{loanId}/cancel")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public void cancelLoan(@PathVariable Long loanId) {
        loanService.cancelLoan(loanId);
    }


    // read all installments for one loan
    @GetMapping("/loans/{loanId}/installments")
    public List<LoanInstallmentResponse> getInstallmentsForLoan(@PathVariable Long loanId) {
        return loanService.getInstallmentsForLoan(loanId);
    }

}
