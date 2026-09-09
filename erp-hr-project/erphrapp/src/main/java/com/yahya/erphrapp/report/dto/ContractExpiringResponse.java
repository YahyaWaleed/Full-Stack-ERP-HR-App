package com.yahya.erphrapp.report.dto;

import java.time.LocalDate;

public class ContractExpiringResponse {
    private String empCode, fullNameAr, contractNo, contractType;
    private LocalDate startDate, endDate;
    private Long daysLeft;

    public String getEmpCode() { return empCode; }
    public void setEmpCode(String empCode) { this.empCode = empCode; }
    public String getFullNameAr() { return fullNameAr; }
    public void setFullNameAr(String fullNameAr) { this.fullNameAr = fullNameAr; }
    public String getContractNo() { return contractNo; }
    public void setContractNo(String contractNo) { this.contractNo = contractNo; }
    public String getContractType() { return contractType; }
    public void setContractType(String contractType) { this.contractType = contractType; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Long getDaysLeft() { return daysLeft; }
    public void setDaysLeft(Long daysLeft) { this.daysLeft = daysLeft; }
}