package com.yahya.erphrapp.payroll.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payroll_settings")
public class PayrollSetting {

    @Id
    @Column(name = "fiscal_year")
    private int fiscalYear;

    @Column(name = "personal_exemption", nullable = false)
    private BigDecimal personalExemption;

    @Column(name = "ins_min_wage", nullable = false)
    private BigDecimal insMinWage;

    @Column(name = "ins_max_wage", nullable = false)
    private BigDecimal insMaxWage;

    @Column(name = "ins_employee_rate", nullable = false)
    private BigDecimal insEmployeeRate;

    @Column(name = "ins_employer_rate", nullable = false)
    private BigDecimal insEmployerRate;

    @Column(name = "working_days_month", nullable = false)
    private int workingDaysMonth;

    @Column(name = "daily_hours", nullable = false)
    private BigDecimal dailyHours;

    @Column(name = "overtime_factor", nullable = false)
    private BigDecimal overtimeFactor;

    public PayrollSetting() {}

    public int getFiscalYear() { return fiscalYear; }
    public void setFiscalYear(int fiscalYear) { this.fiscalYear = fiscalYear; }

    public BigDecimal getPersonalExemption() { return personalExemption; }
    public void setPersonalExemption(BigDecimal personalExemption) { this.personalExemption = personalExemption; }

    public BigDecimal getInsMinWage() { return insMinWage; }
    public void setInsMinWage(BigDecimal insMinWage) { this.insMinWage = insMinWage; }

    public BigDecimal getInsMaxWage() { return insMaxWage; }
    public void setInsMaxWage(BigDecimal insMaxWage) { this.insMaxWage = insMaxWage; }

    public BigDecimal getInsEmployeeRate() { return insEmployeeRate; }
    public void setInsEmployeeRate(BigDecimal insEmployeeRate) { this.insEmployeeRate = insEmployeeRate; }

    public BigDecimal getInsEmployerRate() { return insEmployerRate; }
    public void setInsEmployerRate(BigDecimal insEmployerRate) { this.insEmployerRate = insEmployerRate; }

    public int getWorkingDaysMonth() { return workingDaysMonth; }
    public void setWorkingDaysMonth(int workingDaysMonth) { this.workingDaysMonth = workingDaysMonth; }

    public BigDecimal getDailyHours() { return dailyHours; }
    public void setDailyHours(BigDecimal dailyHours) { this.dailyHours = dailyHours; }

    public BigDecimal getOvertimeFactor() { return overtimeFactor; }
    public void setOvertimeFactor(BigDecimal overtimeFactor) { this.overtimeFactor = overtimeFactor; }
}