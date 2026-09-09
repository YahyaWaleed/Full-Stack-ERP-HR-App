package com.yahya.erphrapp.loans.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LoanInstallmentRequest {

    private long loanId;
    private long payslipId;
    private String periodCode;
    private BigDecimal amount;
    private LocalDate paidOn;

    public long getLoanId() {
        return loanId;
    }

    public void setLoanId(long loanId) {
        this.loanId = loanId;
    }

    public long getPayslipId() {
        return payslipId;
    }

    public void setPayslipId(long payslipId) {
        this.payslipId = payslipId;
    }

    public String getPeriodCode() {
        return periodCode;
    }

    public void setPeriodCode(String periodCode) {
        this.periodCode = periodCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getPaidOn() {
        return paidOn;
    }

    public void setPaidOn(LocalDate paidOn) {
        this.paidOn = paidOn;
    }
}
