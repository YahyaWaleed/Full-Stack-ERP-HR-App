package com.yahya.erphrapp.report.dto;

import java.math.BigDecimal;

public class LeaveBalanceReportResponse {
    private String empCode, fullNameAr, department, leaveType;
    private Integer fiscalYear;
    private BigDecimal entitledDays, carriedForward, usedDays, remainingDays;

    public String getEmpCode() { return empCode; }
    public void setEmpCode(String empCode) { this.empCode = empCode; }
    public String getFullNameAr() { return fullNameAr; }
    public void setFullNameAr(String fullNameAr) { this.fullNameAr = fullNameAr; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getLeaveType() { return leaveType; }
    public void setLeaveType(String leaveType) { this.leaveType = leaveType; }
    public Integer getFiscalYear() { return fiscalYear; }
    public void setFiscalYear(Integer fiscalYear) { this.fiscalYear = fiscalYear; }
    public BigDecimal getEntitledDays() { return entitledDays; }
    public void setEntitledDays(BigDecimal entitledDays) { this.entitledDays = entitledDays; }
    public BigDecimal getCarriedForward() { return carriedForward; }
    public void setCarriedForward(BigDecimal carriedForward) { this.carriedForward = carriedForward; }
    public BigDecimal getUsedDays() { return usedDays; }
    public void setUsedDays(BigDecimal usedDays) { this.usedDays = usedDays; }
    public BigDecimal getRemainingDays() { return remainingDays; }
    public void setRemainingDays(BigDecimal remainingDays) { this.remainingDays = remainingDays; }
}