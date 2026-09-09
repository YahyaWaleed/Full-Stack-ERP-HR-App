package com.yahya.erphrapp.attendance.dto;

import java.math.BigDecimal;

public class AttendanceSummaryResponse {
    private Integer id;
    private Long periodId;
    private Long empId;
    private BigDecimal workingDays;
    private BigDecimal presentDays;
    private BigDecimal paidLeaveDays;
    private BigDecimal unpaidAbsentDays;
    private BigDecimal overtimeHours;
    private Integer lateMinutes;
    private String periodCode;


   public AttendanceSummaryResponse() {};

    public String getPeriodCode() { return periodCode; }
    public void setPeriodCode(String periodCode) { this.periodCode = periodCode; }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(Long periodId) {
        this.periodId = periodId;
    }

    public Long getEmpId() {
        return empId;
    }

    public void setEmpId(Long empId) {
        this.empId = empId;
    }

    public BigDecimal getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(BigDecimal workingDays) {
        this.workingDays = workingDays;
    }

    public BigDecimal getPresentDays() {
        return presentDays;
    }

    public void setPresentDays(BigDecimal presentDays) {
        this.presentDays = presentDays;
    }

    public BigDecimal getPaidLeaveDays() {
        return paidLeaveDays;
    }

    public void setPaidLeaveDays(BigDecimal paidLeaveDays) {
        this.paidLeaveDays = paidLeaveDays;
    }

    public BigDecimal getUnpaidAbsentDays() {
        return unpaidAbsentDays;
    }

    public void setUnpaidAbsentDays(BigDecimal unpaidAbsentDays) {
        this.unpaidAbsentDays = unpaidAbsentDays;
    }

    public BigDecimal getOvertimeHours() {
        return overtimeHours;
    }

    public void setOvertimeHours(BigDecimal overtimeHours) {
        this.overtimeHours = overtimeHours;
    }

    public Integer getLateMinutes() {
        return lateMinutes;
    }

    public void setLateMinutes(Integer lateMinutes) {
        this.lateMinutes = lateMinutes;
    }
}
