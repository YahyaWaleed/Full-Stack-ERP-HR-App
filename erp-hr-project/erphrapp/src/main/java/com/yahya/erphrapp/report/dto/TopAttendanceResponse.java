package com.yahya.erphrapp.report.dto;

public class TopAttendanceResponse {
    private String empCode;
    private String fullNameAr;
    private java.math.BigDecimal presentDays;

    public String getEmpCode() { return empCode; }
    public void setEmpCode(String empCode) { this.empCode = empCode; }

    public String getFullNameAr() { return fullNameAr; }
    public void setFullNameAr(String fullNameAr) { this.fullNameAr = fullNameAr; }

    public java.math.BigDecimal getPresentDays() { return presentDays; }
    public void setPresentDays(java.math.BigDecimal presentDays) { this.presentDays = presentDays; }
}