package com.yahya.erphrapp.employee.dto;

import com.yahya.erphrapp.employee.entity.EmployeeContract;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmployeeContractRequest {

    @NotNull
    private EmployeeContract.ContractType contractType;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull
    @Positive
    @Digits(integer = 10, fraction = 2)
    private BigDecimal basicSalary;

    @Pattern(regexp = "[A-Z]{3}", message = "must be a 3-letter currency code")
    private String currency; // defaults to EGP

    @DecimalMin("1.0")
    @DecimalMax("80.0")
    private BigDecimal weeklyHours; // defaults to 40

    @Min(0)
    @Max(60)
    private Integer annualLeaveDays; // defaults to 21

    @Min(0)
    @Max(12)
    private Integer probationMonths; // defaults to 3

    @Size(max = 255)
    private String notes;

    public EmployeeContract.ContractType getContractType() { return contractType; }
    public void setContractType(EmployeeContract.ContractType contractType) { this.contractType = contractType; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getBasicSalary() { return basicSalary; }
    public void setBasicSalary(BigDecimal basicSalary) { this.basicSalary = basicSalary; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getWeeklyHours() { return weeklyHours; }
    public void setWeeklyHours(BigDecimal weeklyHours) { this.weeklyHours = weeklyHours; }

    public Integer getAnnualLeaveDays() { return annualLeaveDays; }
    public void setAnnualLeaveDays(Integer annualLeaveDays) { this.annualLeaveDays = annualLeaveDays; }

    public Integer getProbationMonths() { return probationMonths; }
    public void setProbationMonths(Integer probationMonths) { this.probationMonths = probationMonths; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
