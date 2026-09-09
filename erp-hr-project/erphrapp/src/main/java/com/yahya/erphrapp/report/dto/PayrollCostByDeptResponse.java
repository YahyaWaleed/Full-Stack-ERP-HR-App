package com.yahya.erphrapp.report.dto;

import java.math.BigDecimal;

public class PayrollCostByDeptResponse {
    private String periodCode, department;
    private Long employees;
    private BigDecimal totalBasic, totalGross, totalTax, insuranceEmployee, insuranceEmployer, totalNet, companyCost;

    public String getPeriodCode() { return periodCode; }
    public void setPeriodCode(String periodCode) { this.periodCode = periodCode; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public Long getEmployees() { return employees; }
    public void setEmployees(Long employees) { this.employees = employees; }
    public BigDecimal getTotalBasic() { return totalBasic; }
    public void setTotalBasic(BigDecimal totalBasic) { this.totalBasic = totalBasic; }
    public BigDecimal getTotalGross() { return totalGross; }
    public void setTotalGross(BigDecimal totalGross) { this.totalGross = totalGross; }
    public BigDecimal getTotalTax() { return totalTax; }
    public void setTotalTax(BigDecimal totalTax) { this.totalTax = totalTax; }
    public BigDecimal getInsuranceEmployee() { return insuranceEmployee; }
    public void setInsuranceEmployee(BigDecimal insuranceEmployee) { this.insuranceEmployee = insuranceEmployee; }
    public BigDecimal getInsuranceEmployer() { return insuranceEmployer; }
    public void setInsuranceEmployer(BigDecimal insuranceEmployer) { this.insuranceEmployer = insuranceEmployer; }
    public BigDecimal getTotalNet() { return totalNet; }
    public void setTotalNet(BigDecimal totalNet) { this.totalNet = totalNet; }
    public BigDecimal getCompanyCost() { return companyCost; }
    public void setCompanyCost(BigDecimal companyCost) { this.companyCost = companyCost; }
}