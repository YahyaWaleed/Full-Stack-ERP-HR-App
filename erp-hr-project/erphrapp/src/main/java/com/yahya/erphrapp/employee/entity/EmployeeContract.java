package com.yahya.erphrapp.employee.entity;

import com.yahya.erphrapp.exception.BadRequestException;
import com.yahya.erphrapp.exception.ConflictException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employee_contracts")
public class EmployeeContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @Column(name = "contract_no")
    private String contractNo;

    public enum ContractType {
        PERMANENT, FIXED_TERM, PART_TIME, CONSULTANT, INTERN
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type", nullable = false)
    private ContractType contractType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "basic_salary", nullable = false)
    private BigDecimal basicSalary;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "weekly_hours", nullable = false)
    private BigDecimal weeklyHours;

    @Column(name = "annual_leave_days", nullable = false)
    private int annualLeaveDays;

    @Column(name = "probation_months", nullable = false)
    private int probationMonths;

    public enum ContractStatus {
        ACTIVE, EXPIRED, TERMINATED
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContractStatus status;

    @Column(name = "notes")
    private String notes;

    // the end date the contract was signed with; end_date may later be shortened by a renewal or termination
    @Column(name = "original_end_date")
    private LocalDate originalEndDate;

    @Version
    @Column(name = "version", nullable = false)
    private int version;

    public EmployeeContract() {}

    // ---- behaviour -------------------------------------------------------

    public boolean isActive() {
        return status == ContractStatus.ACTIVE;
    }

    // closes the contract on lastDay (never extends it), remembering the originally agreed end date
    public void closeOn(LocalDate lastDay, ContractStatus newStatus) {
        if (!isActive()) {
            throw new ConflictException("Contract " + contractNo + " is not active");
        }
        if (lastDay.isBefore(startDate)) {
            throw new BadRequestException("Contract " + contractNo + " cannot end before it starts (" + startDate + ")");
        }
        if (originalEndDate == null) {
            originalEndDate = endDate;
        }
        if (endDate == null || lastDay.isBefore(endDate)) {
            endDate = lastDay;
        }
        status = newStatus;
    }

    public LocalDate getOriginalEndDate() { return originalEndDate; }
    public int getVersion() { return version; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public String getContractNo() { return contractNo; }
    public void setContractNo(String contractNo) { this.contractNo = contractNo; }

    public ContractType getContractType() { return contractType; }
    public void setContractType(ContractType contractType) { this.contractType = contractType; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getBasicSalary() { return basicSalary; }
    public void setBasicSalary(BigDecimal basicSalary) { this.basicSalary = basicSalary; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getWeeklyHours() { return weeklyHours; }
    public void setWeeklyHours(BigDecimal weeklyHours) { this.weeklyHours = weeklyHours; }

    public int getAnnualLeaveDays() { return annualLeaveDays; }
    public void setAnnualLeaveDays(int annualLeaveDays) { this.annualLeaveDays = annualLeaveDays; }

    public int getProbationMonths() { return probationMonths; }
    public void setProbationMonths(int probationMonths) { this.probationMonths = probationMonths; }

    public ContractStatus getStatus() { return status; }
    public void setStatus(ContractStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
