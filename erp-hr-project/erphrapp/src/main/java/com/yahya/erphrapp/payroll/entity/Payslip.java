package com.yahya.erphrapp.payroll.entity;

import com.yahya.erphrapp.employee.entity.Employee;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payslips")
public class Payslip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payslip_id")
    private Long id;

    @Column(name = "payslip_no", nullable = false, unique = true)
    private String payslipNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_id", nullable = false)
    private PayrollPeriod period;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @Column(name = "basic_salary", nullable = false)
    private BigDecimal basicSalary;

    @Column(name = "total_earnings", nullable = false)
    private BigDecimal totalEarnings;

    @Column(name = "total_deductions", nullable = false)
    private BigDecimal totalDeductions;

    @Column(name = "taxable_income", nullable = false)
    private BigDecimal taxableIncome;

    @Column(name = "income_tax", nullable = false)
    private BigDecimal incomeTax;

    @Column(name = "insurance_employee", nullable = false)
    private BigDecimal insuranceEmployee;

    @Column(name = "insurance_employer", nullable = false)
    private BigDecimal insuranceEmployer;

    @Column(name = "net_pay", nullable = false)
    private BigDecimal netPay;

    @Column(name = "worked_days", nullable = false)
    private BigDecimal workedDays;

    @Column(name = "absent_days", nullable = false)
    private BigDecimal absentDays;

    @Column(name = "overtime_hours", nullable = false)
    private BigDecimal overtimeHours;

    @Column(name = "currency", nullable = false)
    private String currency;

    public enum PayslipStatus {
        DRAFT, APPROVED, PAID, CANCELLED
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PayslipStatus status;

    @Column(name = "generated_at", insertable = false, updatable = false)
    private LocalDateTime generatedAt;

    public Payslip() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPayslipNo() { return payslipNo; }
    public void setPayslipNo(String payslipNo) { this.payslipNo = payslipNo; }

    public PayrollPeriod getPeriod() { return period; }
    public void setPeriod(PayrollPeriod period) { this.period = period; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

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

    public PayslipStatus getStatus() { return status; }
    public void setStatus(PayslipStatus status) { this.status = status; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
}