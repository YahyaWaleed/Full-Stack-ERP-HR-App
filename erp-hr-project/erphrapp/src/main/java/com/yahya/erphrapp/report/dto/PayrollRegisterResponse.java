package com.yahya.erphrapp.report.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PayrollRegisterResponse {
    private Long payslipId;
    private String payslipNo, periodCode, empCode, fullNameAr, department, jobTitle, status, currency;
    private LocalDate payDate;
    private BigDecimal basicSalary, grossPay, totalDeductions, insuranceEmployee, incomeTax, netPay,
            workedDays, absentDays, overtimeHours, insuranceEmployer, totalCompanyCost;

    public Long getPayslipId() { return payslipId; }
    public void setPayslipId(Long payslipId) { this.payslipId = payslipId; }
    public String getPayslipNo() { return payslipNo; }
    public void setPayslipNo(String payslipNo) { this.payslipNo = payslipNo; }
    public String getPeriodCode() { return periodCode; }
    public void setPeriodCode(String periodCode) { this.periodCode = periodCode; }
    public LocalDate getPayDate() { return payDate; }
    public void setPayDate(LocalDate payDate) { this.payDate = payDate; }
    public String getEmpCode() { return empCode; }
    public void setEmpCode(String empCode) { this.empCode = empCode; }
    public String getFullNameAr() { return fullNameAr; }
    public void setFullNameAr(String fullNameAr) { this.fullNameAr = fullNameAr; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public BigDecimal getBasicSalary() { return basicSalary; }
    public void setBasicSalary(BigDecimal basicSalary) { this.basicSalary = basicSalary; }
    public BigDecimal getGrossPay() { return grossPay; }
    public void setGrossPay(BigDecimal grossPay) { this.grossPay = grossPay; }
    public BigDecimal getTotalDeductions() { return totalDeductions; }
    public void setTotalDeductions(BigDecimal totalDeductions) { this.totalDeductions = totalDeductions; }
    public BigDecimal getInsuranceEmployee() { return insuranceEmployee; }
    public void setInsuranceEmployee(BigDecimal insuranceEmployee) { this.insuranceEmployee = insuranceEmployee; }
    public BigDecimal getIncomeTax() { return incomeTax; }
    public void setIncomeTax(BigDecimal incomeTax) { this.incomeTax = incomeTax; }
    public BigDecimal getNetPay() { return netPay; }
    public void setNetPay(BigDecimal netPay) { this.netPay = netPay; }
    public BigDecimal getWorkedDays() { return workedDays; }
    public void setWorkedDays(BigDecimal workedDays) { this.workedDays = workedDays; }
    public BigDecimal getAbsentDays() { return absentDays; }
    public void setAbsentDays(BigDecimal absentDays) { this.absentDays = absentDays; }
    public BigDecimal getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(BigDecimal overtimeHours) { this.overtimeHours = overtimeHours; }
    public BigDecimal getInsuranceEmployer() { return insuranceEmployer; }
    public void setInsuranceEmployer(BigDecimal insuranceEmployer) { this.insuranceEmployer = insuranceEmployer; }
    public BigDecimal getTotalCompanyCost() { return totalCompanyCost; }
    public void setTotalCompanyCost(BigDecimal totalCompanyCost) { this.totalCompanyCost = totalCompanyCost; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}