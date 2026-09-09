package com.yahya.erphrapp.payroll.entity;

import com.yahya.erphrapp.employee.entity.Employee;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payroll_periods")
public class PayrollPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "period_id")
    private Long id;

    @Column(name = "period_code", nullable = false, unique = true)
    private String periodCode;

    @Column(name = "fiscal_year", nullable = false)
    private int fiscalYear;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "pay_date", nullable = false)
    private LocalDate payDate;

    @Column(name = "working_days", nullable = false)
    private int workingDays;

    public enum PeriodStatus {
        OPEN, PROCESSED, APPROVED, PAID, CLOSED
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PeriodStatus status;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by", nullable = true)
    private Employee approvedBy;

    public PayrollPeriod() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPeriodCode() { return periodCode; }
    public void setPeriodCode(String periodCode) { this.periodCode = periodCode; }

    public int getFiscalYear() { return fiscalYear; }
    public void setFiscalYear(int fiscalYear) { this.fiscalYear = fiscalYear; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDate getPayDate() { return payDate; }
    public void setPayDate(LocalDate payDate) { this.payDate = payDate; }

    public int getWorkingDays() { return workingDays; }
    public void setWorkingDays(int workingDays) { this.workingDays = workingDays; }

    public PeriodStatus getStatus() { return status; }
    public void setStatus(PeriodStatus status) { this.status = status; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }

    public Employee getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Employee approvedBy) { this.approvedBy = approvedBy; }
}