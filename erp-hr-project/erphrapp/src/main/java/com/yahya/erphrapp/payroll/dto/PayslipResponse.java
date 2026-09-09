package com.yahya.erphrapp.payroll.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PayslipResponse {

    private Long id;
    private String payslipNo;
    private String periodCode;
    private String empCode;
    private String employeeName;
    private BigDecimal basicSalary;
    private BigDecimal totalEarnings;
    private BigDecimal totalDeductions;
    private BigDecimal taxableIncome;
    private BigDecimal incomeTax;
    private BigDecimal insuranceEmployee;
    private BigDecimal insuranceEmployer;
    private BigDecimal netPay;
    private BigDecimal workedDays;
    private BigDecimal absentDays;
    private BigDecimal overtimeHours;
    private String currency;
    private String status;
    private LocalDateTime generatedAt;
    private List<PayslipLineResponse> lines;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPayslipNo() { return payslipNo; }
    public void setPayslipNo(String payslipNo) { this.payslipNo = payslipNo; }

    public String getPeriodCode() { return periodCode; }
    public void setPeriodCode(String periodCode) { this.periodCode = periodCode; }

    public String getEmpCode() { return empCode; }
    public void setEmpCode(String empCode) { this.empCode = empCode; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public BigDecimal getBasicSalary() { return basicSalary; }
    public void setBasicSalary(BigDecimal basicSalary) { this.basicSalary = basicSalary; }

    public BigDecimal getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(BigDecimal totalEarnings) { this.totalEarnings = totalEarnings; }

    public BigDecimal getTotalDeductions() { return totalDeductions; }
    public void setTotalDeductions(BigDecimal totalDeductions) { this.totalDeductions = totalDeductions; }

    public BigDecimal getTaxableIncome() { return taxableIncome; }
    public void setTaxableIncome(BigDecimal taxableIncome) { this.taxableIncome = taxableIncome; }

    public BigDecimal getIncomeTax() { return incomeTax; }
    public void setIncomeTax(BigDecimal incomeTax) { this.incomeTax = incomeTax; }

    public BigDecimal getInsuranceEmployee() { return insuranceEmployee; }
    public void setInsuranceEmployee(BigDecimal insuranceEmployee) { this.insuranceEmployee = insuranceEmployee; }

    public BigDecimal getInsuranceEmployer() { return insuranceEmployer; }
    public void setInsuranceEmployer(BigDecimal insuranceEmployer) { this.insuranceEmployer = insuranceEmployer; }

    public BigDecimal getNetPay() { return netPay; }
    public void setNetPay(BigDecimal netPay) { this.netPay = netPay; }

    public BigDecimal getWorkedDays() { return workedDays; }
    public void setWorkedDays(BigDecimal workedDays) { this.workedDays = workedDays; }

    public BigDecimal getAbsentDays() { return absentDays; }
    public void setAbsentDays(BigDecimal absentDays) { this.absentDays = absentDays; }

    public BigDecimal getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(BigDecimal overtimeHours) { this.overtimeHours = overtimeHours; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public List<PayslipLineResponse> getLines() { return lines; }
    public void setLines(List<PayslipLineResponse> lines) { this.lines = lines; }
}