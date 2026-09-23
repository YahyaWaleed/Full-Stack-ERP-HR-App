package com.yahya.erphrapp.payroll.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class PayrollPeriodRequest {

    @NotBlank
    @Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])", message = "must be a period code like 2026-09")
    private String periodCode;

    @NotNull
    @Min(2000)
    @Max(2100)
    private Integer fiscalYear;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private LocalDate payDate;

    @Positive
    @Max(31)
    private Integer workingDays;

    public String getPeriodCode() { return periodCode; }
    public void setPeriodCode(String periodCode) { this.periodCode = periodCode; }

    public Integer getFiscalYear() { return fiscalYear; }
    public void setFiscalYear(Integer fiscalYear) { this.fiscalYear = fiscalYear; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDate getPayDate() { return payDate; }
    public void setPayDate(LocalDate payDate) { this.payDate = payDate; }

    public Integer getWorkingDays() { return workingDays; }
    public void setWorkingDays(Integer workingDays) { this.workingDays = workingDays; }
}
