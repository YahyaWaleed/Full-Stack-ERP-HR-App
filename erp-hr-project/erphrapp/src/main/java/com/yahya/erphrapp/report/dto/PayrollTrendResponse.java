package com.yahya.erphrapp.report.dto;

import java.math.BigDecimal;

public class PayrollTrendResponse {
    private String periodCode;
    private Long employees;
    private BigDecimal gross, deductions, net, companyCost;

    public String getPeriodCode() { return periodCode; }
    public void setPeriodCode(String periodCode) { this.periodCode = periodCode; }
    public Long getEmployees() { return employees; }
    public void setEmployees(Long employees) { this.employees = employees; }
    public BigDecimal getGross() { return gross; }
    public void setGross(BigDecimal gross) { this.gross = gross; }
    public BigDecimal getDeductions() { return deductions; }
    public void setDeductions(BigDecimal deductions) { this.deductions = deductions; }
    public BigDecimal getNet() { return net; }
    public void setNet(BigDecimal net) { this.net = net; }
    public BigDecimal getCompanyCost() { return companyCost; }
    public void setCompanyCost(BigDecimal companyCost) { this.companyCost = companyCost; }
}