package com.yahya.erphrapp.loans.entity;

import com.yahya.erphrapp.employee.entity.Employee;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

   public enum LoanType {
       ADVANCE, PERSONAL, EMERGENCY, HOUSING
   }

   @Enumerated(EnumType.STRING)
    @Column(name = "loan_type", nullable = false)
    private LoanType type;

   @Column(name = "principal_amount")
    private BigDecimal principalAmount;

   @Column(name = "installments_count")
    private int installmentsCount;

   @Column(name = "monthly_installment")
    private BigDecimal monthlyInstallment;

   @Column(name = "remaining_balance")
    private BigDecimal remainingBalance;

   @Column(name = "start_period")
    private String startPeriod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by", nullable = true)
    private Employee approvedBy;

    public enum LoanStatus {
        ACTIVE,CLOSED,CANCELLED
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status" , nullable = false)
    private LoanStatus status;

    @Column(name = "request_date")
    private LocalDate requestDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LoanType getType() {
        return type;
    }

    public void setType(LoanType type) {
        this.type = type;
    }

    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    public int getInstallmentsCount() {
        return installmentsCount;
    }

    public void setInstallmentsCount(int installmentsCount) {
        this.installmentsCount = installmentsCount;
    }

    public BigDecimal getMonthlyInstallment() {
        return monthlyInstallment;
    }

    public void setMonthlyInstallment(BigDecimal monthlyInstallment) {
        this.monthlyInstallment = monthlyInstallment;
    }

    public BigDecimal getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(BigDecimal remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public String getStartPeriod() {
        return startPeriod;
    }

    public void setStartPeriod(String startPeriod) {
        this.startPeriod = startPeriod;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public Employee getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Employee approvedBy) {
        this.approvedBy = approvedBy;
    }
}
