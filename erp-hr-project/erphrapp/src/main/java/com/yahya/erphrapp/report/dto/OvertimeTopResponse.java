package com.yahya.erphrapp.report.dto;

import java.math.BigDecimal;

public class OvertimeTopResponse {
    private String empCode, fullNameAr;
    private BigDecimal otHours, otPaid;

    public String getEmpCode() { return empCode; }
    public void setEmpCode(String empCode) { this.empCode = empCode; }
    public String getFullNameAr() { return fullNameAr; }
    public void setFullNameAr(String fullNameAr) { this.fullNameAr = fullNameAr; }
    public BigDecimal getOtHours() { return otHours; }
    public void setOtHours(BigDecimal otHours) { this.otHours = otHours; }
    public BigDecimal getOtPaid() { return otPaid; }
    public void setOtPaid(BigDecimal otPaid) { this.otPaid = otPaid; }
}