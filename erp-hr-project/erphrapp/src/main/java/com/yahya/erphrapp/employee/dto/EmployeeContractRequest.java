package com.yahya.erphrapp.employee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmployeeContractRequest {

    @NotNull
    private String contractType;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull
    @Positive
    private BigDecimal basicSalary;

    private String currency;

    private BigDecimal weeklyHours;

    private Integer annualLeaveDays;

    private Integer probationMonths;

    private String notes;

    public String getContractType() { return contractType; }
    public void setContractType(String contractType) { this.contractType = contractType; }

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