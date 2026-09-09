package com.yahya.erphrapp.payroll.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PayrollPaymentsResponse {

    private Long id;
    private Long payslipId;
    private String empCode;       // from employee
    private String periodCode;    // from payroll period
    private String method;
    private String bankName;
    private String bankAccount;
    private BigDecimal amount;
    private LocalDate paidOn;
    private String reference;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPayslipId() { return payslipId; }
    public void setPayslipId(Long payslipId) { this.payslipId = payslipId; }

    public String getEmpCode() { return empCode; }
    public void setEmpCode(String empCode) { this.empCode = empCode; }

    public String getPeriodCode() { return periodCode; }
    public void setPeriodCode(String periodCode) { this.periodCode = periodCode; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getBankAccount() { return bankAccount; }
    public void setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getPaidOn() { return paidOn; }
    public void setPaidOn(LocalDate paidOn) { this.paidOn = paidOn; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
}