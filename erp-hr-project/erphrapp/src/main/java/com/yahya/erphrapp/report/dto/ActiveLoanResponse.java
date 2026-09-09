package com.yahya.erphrapp.report.dto;

import java.math.BigDecimal;

public class ActiveLoanResponse {
    private Long loanId;
    private String empCode, fullNameAr, loanType, startPeriod, status;
    private Integer installmentsCount;
    private BigDecimal principalAmount, monthlyInstallment, remainingBalance, paidPct;

    public Long getLoanId() { return loanId; }
    public void setLoanId(Long loanId) { this.loanId = loanId; }
    public String getEmpCode() { return empCode; }
    public void setEmpCode(String empCode) { this.empCode = empCode; }
    public String getFullNameAr() { return fullNameAr; }
    public void setFullNameAr(String fullNameAr) { this.fullNameAr = fullNameAr; }
    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }
    public BigDecimal getPrincipalAmount() { return principalAmount; }
    public void setPrincipalAmount(BigDecimal principalAmount) { this.principalAmount = principalAmount; }
    public Integer getInstallmentsCount() { return installmentsCount; }
    public void setInstallmentsCount(Integer installmentsCount) { this.installmentsCount = installmentsCount; }
    public BigDecimal getMonthlyInstallment() { return monthlyInstallment; }
    public void setMonthlyInstallment(BigDecimal monthlyInstallment) { this.monthlyInstallment = monthlyInstallment; }
    public BigDecimal getRemainingBalance() { return remainingBalance; }
    public void setRemainingBalance(BigDecimal remainingBalance) { this.remainingBalance = remainingBalance; }
    public BigDecimal getPaidPct() { return paidPct; }
    public void setPaidPct(BigDecimal paidPct) { this.paidPct = paidPct; }
    public String getStartPeriod() { return startPeriod; }
    public void setStartPeriod(String startPeriod) { this.startPeriod = startPeriod; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}