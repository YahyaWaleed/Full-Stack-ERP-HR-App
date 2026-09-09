package com.yahya.erphrapp.employee.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmployeeContractResponse {

    private Long id;
    private String empCode;
    private String contractNo;
    private String contractType;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal basicSalary;
    private String currency;
    private BigDecimal weeklyHours;
    private int annualLeaveDays;
    private int probationMonths;
    private String status;
    private String notes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmpCode() { return empCode; }
    public void setEmpCode(String empCode) { this.empCode = empCode; }

    public String getContractNo() { return contractNo; }
    public void setContractNo(String contractNo) { this.contractNo = contractNo; }

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

    public int getAnnualLeaveDays() { return annualLeaveDays; }
    public void setAnnualLeaveDays(int annualLeaveDays) { this.annualLeaveDays = annualLeaveDays; }

    public int getProbationMonths() { return probationMonths; }
    public void setProbationMonths(int probationMonths) { this.probationMonths = probationMonths; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}