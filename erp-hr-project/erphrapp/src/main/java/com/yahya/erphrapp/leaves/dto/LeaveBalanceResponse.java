package com.yahya.erphrapp.leaves.dto;

import java.math.BigDecimal;

public class LeaveBalanceResponse {

    private Long id;
    private String empCode;
    private String employeeName;
    private String leaveTypeName;
    private int fiscalYear;
    private BigDecimal entitledDays;
    private BigDecimal carriedForward;
    private BigDecimal usedDays;
    private BigDecimal remainingDays;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmpCode() { return empCode; }
    public void setEmpCode(String empCode) { this.empCode = empCode; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getLeaveTypeName() { return leaveTypeName; }
    public void setLeaveTypeName(String leaveTypeName) { this.leaveTypeName = leaveTypeName; }

    public int getFiscalYear() { return fiscalYear; }
    public void setFiscalYear(int fiscalYear) { this.fiscalYear = fiscalYear; }

    public BigDecimal getEntitledDays() { return entitledDays; }
    public void setEntitledDays(BigDecimal entitledDays) { this.entitledDays = entitledDays; }

    public BigDecimal getCarriedForward() { return carriedForward; }
    public void setCarriedForward(BigDecimal carriedForward) { this.carriedForward = carriedForward; }

    public BigDecimal getUsedDays() { return usedDays; }
    public void setUsedDays(BigDecimal usedDays) { this.usedDays = usedDays; }

    public BigDecimal getRemainingDays() { return remainingDays; }
    public void setRemainingDays(BigDecimal remainingDays) { this.remainingDays = remainingDays; }
}