package com.yahya.erphrapp.loans.dto;

import com.yahya.erphrapp.loans.entity.Loan;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

// status and remaining balance are never client-set: a new loan is ACTIVE and owes its full principal
public class LoanRequest {

    @NotNull
    private Long empId;

    @NotNull
    private Loan.LoanType type;

    @NotNull
    @Positive
    @Digits(integer = 10, fraction = 2)
    private BigDecimal principalAmount;

    @Min(1)
    @Max(120)
    private int installmentsCount;

    @NotNull
    @Positive
    @Digits(integer = 10, fraction = 2)
    private BigDecimal monthlyInstallment;

    @NotBlank
    @Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])", message = "must be a period code like 2026-09")
    private String startPeriod;

    private Long approvedById;

    @NotNull
    @PastOrPresent
    private LocalDate requestDate;

    public Long getEmpId() { return empId; }
    public void setEmpId(Long empId) { this.empId = empId; }

    public Loan.LoanType getType() { return type; }
    public void setType(Loan.LoanType type) { this.type = type; }

    public BigDecimal getPrincipalAmount() { return principalAmount; }
    public void setPrincipalAmount(BigDecimal principalAmount) { this.principalAmount = principalAmount; }

    public int getInstallmentsCount() { return installmentsCount; }
    public void setInstallmentsCount(int installmentsCount) { this.installmentsCount = installmentsCount; }

    public BigDecimal getMonthlyInstallment() { return monthlyInstallment; }
    public void setMonthlyInstallment(BigDecimal monthlyInstallment) { this.monthlyInstallment = monthlyInstallment; }

    public String getStartPeriod() { return startPeriod; }
    public void setStartPeriod(String startPeriod) { this.startPeriod = startPeriod; }

    public Long getApprovedById() { return approvedById; }
    public void setApprovedById(Long approvedById) { this.approvedById = approvedById; }

    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }
}
