package com.yahya.erphrapp.payroll.dto;

import java.math.BigDecimal;

public class PayrollSettingResponse {

    private int fiscalYear;
    private BigDecimal personalExemption;
    private BigDecimal insMinWage;
    private BigDecimal insMaxWage;
    private BigDecimal insEmployeeRate;
    private BigDecimal insEmployerRate;
    private int workingDaysMonth;
    private BigDecimal dailyHours;
    private BigDecimal overtimeFactor;

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