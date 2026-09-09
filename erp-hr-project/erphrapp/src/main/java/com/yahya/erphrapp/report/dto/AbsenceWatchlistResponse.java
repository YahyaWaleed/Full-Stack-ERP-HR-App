package com.yahya.erphrapp.report.dto;

public class AbsenceWatchlistResponse {
    private String empCode, fullNameAr, department;
    private Long unpaidDays, lateMinutes;

    public String getEmpCode() { return empCode; }
    public void setEmpCode(String empCode) { this.empCode = empCode; }
    public String getFullNameAr() { return fullNameAr; }
    public void setFullNameAr(String fullNameAr) { this.fullNameAr = fullNameAr; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public Long getUnpaidDays() { return unpaidDays; }
    public void setUnpaidDays(Long unpaidDays) { this.unpaidDays = unpaidDays; }
    public Long getLateMinutes() { return lateMinutes; }
    public void setLateMinutes(Long lateMinutes) { this.lateMinutes = lateMinutes; }
}