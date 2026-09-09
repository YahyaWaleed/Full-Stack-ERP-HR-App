package com.yahya.erphrapp.payroll.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PayrollPeriodResponse {

    private Long id;
    private String periodCode;
    private int fiscalYear;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate payDate;
    private int workingDays;
    private String status;
    private LocalDateTime processedAt;
    private String approvedByName; // flattened from approvedBy employee

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPeriodCode() { return periodCode; }
    public void setPeriodCode(String periodCode) { this.periodCode = periodCode; }

    public int getFiscalYear() { return fiscalYear; }
    public void setFiscalYear(int fiscalYear) { this.fiscalYear = fiscalYear; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDate getPayDate() { return payDate; }
    public void setPayDate(LocalDate payDate) { this.payDate = payDate; }

    public int getWorkingDays() { return workingDays; }
    public void setWorkingDays(int workingDays) { this.workingDays = workingDays; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }

    public String getApprovedByName() { return approvedByName; }
    public void setApprovedByName(String approvedByName) { this.approvedByName = approvedByName; }
}