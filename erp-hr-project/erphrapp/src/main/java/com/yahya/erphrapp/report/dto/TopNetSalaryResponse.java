package com.yahya.erphrapp.report.dto;

import java.math.BigDecimal;

public class TopNetSalaryResponse {
    private String empCode;
    private String fullNameAr;
    private BigDecimal netPay;

    public String getEmpCode() { return empCode; }
    public void setEmpCode(String empCode) { this.empCode = empCode; }

    public String getFullNameAr() { return fullNameAr; }
    public void setFullNameAr(String fullNameAr) { this.fullNameAr = fullNameAr; }

    public BigDecimal getNetPay() { return netPay; }
    public void setNetPay(BigDecimal netPay) { this.netPay = netPay; }
}