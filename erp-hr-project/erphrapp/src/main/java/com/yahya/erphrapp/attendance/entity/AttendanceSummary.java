package com.yahya.erphrapp.attendance.entity;

import com.yahya.erphrapp.employee.entity.Employee;
import com.yahya.erphrapp.payroll.entity.PayrollPeriod;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "attendance_summary")
public class AttendanceSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "att_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_id", nullable = false)
    private PayrollPeriod payrollPeriod;

    @Column(name = "working_days")
    private BigDecimal workingDays;

    @Column(name = "present_days")
    private BigDecimal presentDays;

    @Column(name = "paid_leave_days")
    private BigDecimal paidLeaveDays;

    @Column(name = "unpaid_absent_days")
    private BigDecimal unpaidAbsentDays;

    @Column(name = "overtime_hours")
    private BigDecimal overtimeHours;

    @Column(name = "late_minutes")
    private  Integer lateMinutes;

    public AttendanceSummary() {};

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public PayrollPeriod getPayrollPeriod() {
        return payrollPeriod;
    }

    public void setPayrollPeriod(PayrollPeriod payrollPeriod) {
        this.payrollPeriod = payrollPeriod;
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

    public void setPaidLeaveDays(BigDecimal paidLeavesDays) {
        this.paidLeaveDays = paidLeavesDays;
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
