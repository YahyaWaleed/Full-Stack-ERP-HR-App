package com.yahya.erphrapp.report.dto;

import java.math.BigDecimal;

public class TaxInsuranceLiabilityResponse {
    private String periodCode;
    private BigDecimal incomeTaxDue, insuranceEmployeeShare, insuranceEmployerShare, totalInsuranceDue;

    public String getPeriodCode() { return periodCode; }
    public void setPeriodCode(String periodCode) { this.periodCode = periodCode; }
    public BigDecimal getIncomeTaxDue() { return incomeTaxDue; }
    public void setIncomeTaxDue(BigDecimal incomeTaxDue) { this.incomeTaxDue = incomeTaxDue; }
    public BigDecimal getInsuranceEmployeeShare() { return insuranceEmployeeShare; }
    public void setInsuranceEmployeeShare(BigDecimal insuranceEmployeeShare) { this.insuranceEmployeeShare = insuranceEmployeeShare; }
    public BigDecimal getInsuranceEmployerShare() { return insuranceEmployerShare; }
    public void setInsuranceEmployerShare(BigDecimal insuranceEmployerShare) { this.insuranceEmployerShare = insuranceEmployerShare; }
    public BigDecimal getTotalInsuranceDue() { return totalInsuranceDue; }
    public void setTotalInsuranceDue(BigDecimal totalInsuranceDue) { this.totalInsuranceDue = totalInsuranceDue; }
}