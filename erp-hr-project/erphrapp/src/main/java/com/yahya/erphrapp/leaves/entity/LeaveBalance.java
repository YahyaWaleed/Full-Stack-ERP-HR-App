package com.yahya.erphrapp.leaves.entity;

import com.yahya.erphrapp.employee.entity.Employee;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "leave_balances")
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "balance_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private LeaveType leaveType;

    @Column(name = "fiscal_year", nullable = false)
    private int fiscalYear;

    @Column(name = "entitled_days", nullable = false)
    private BigDecimal entitledDays;

    @Column(name = "carried_forward", nullable = false)
    private BigDecimal carriedForward;

    @Column(name = "used_days", nullable = false)
    private BigDecimal usedDays;

    @Column(name = "remaining_days", insertable = false, updatable = false)
    private BigDecimal remainingDays;

    public LeaveBalance() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public LeaveType getLeaveType() { return leaveType; }
    public void setLeaveType(LeaveType leaveType) { this.leaveType = leaveType; }

    public int getFiscalYear() { return fiscalYear; }
    public void setFiscalYear(int fiscalYear) { this.fiscalYear = fiscalYear; }

    public BigDecimal getEntitledDays() { return entitledDays; }
    public void setEntitledDays(BigDecimal entitledDays) { this.entitledDays = entitledDays; }

    public BigDecimal getCarriedForward() { return carriedForward; }
    public void setCarriedForward(BigDecimal carriedForward) { this.carriedForward = carriedForward; }

    public BigDecimal getUsedDays() { return usedDays; }
    public void setUsedDays(BigDecimal usedDays) { this.usedDays = usedDays; }

    public BigDecimal getRemainingDays() { return remainingDays; }
}