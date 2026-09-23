package com.yahya.erphrapp.leaves.entity;

import com.yahya.erphrapp.employee.entity.Employee;
import com.yahya.erphrapp.exception.ConflictException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "leave_requests")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private LeaveType leaveType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "days_count", nullable = false)
    private BigDecimal daysCount;

    @Column(name = "reason")
    private String reason;

    public enum LeaveStatus {
        PENDING, APPROVED, REJECTED, CANCELLED
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LeaveStatus status;

    @Column(name = "applied_on", nullable = false)
    private LocalDate appliedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", nullable = true)
    private Employee approver;

    @Column(name = "decided_on")
    private LocalDate decidedOn;

    @Column(name = "reject_reason")
    private String rejectReason;

    // document reference for leave types that require an attachment (sick note number, link, ...)
    @Column(name = "attachment_ref")
    private String attachmentRef;

    @Version
    @Column(name = "version", nullable = false)
    private int version;

    public LeaveRequest() {}

    // ---- behaviour: only a PENDING request can be decided -----------------

    public void approve(LocalDate today) {
        requirePending("approve");
        status = LeaveStatus.APPROVED;
        decidedOn = today;
    }

    public void reject(String reason, LocalDate today) {
        requirePending("reject");
        status = LeaveStatus.REJECTED;
        rejectReason = reason;
        decidedOn = today;
    }

    public void cancel(LocalDate today) {
        requirePending("cancel");
        status = LeaveStatus.CANCELLED;
        decidedOn = today;
    }

    public boolean isPending() {
        return status == LeaveStatus.PENDING;
    }

    private void requirePending(String action) {
        if (status != LeaveStatus.PENDING) {
            throw new ConflictException("Only a PENDING leave request can be " + action + "d (this one is " + status + ")");
        }
    }

    public String getAttachmentRef() { return attachmentRef; }
    public void setAttachmentRef(String attachmentRef) { this.attachmentRef = attachmentRef; }
    public int getVersion() { return version; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public LeaveType getLeaveType() { return leaveType; }
    public void setLeaveType(LeaveType leaveType) { this.leaveType = leaveType; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getDaysCount() { return daysCount; }
    public void setDaysCount(BigDecimal daysCount) { this.daysCount = daysCount; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LeaveStatus getStatus() { return status; }
    public void setStatus(LeaveStatus status) { this.status = status; }

    public LocalDate getAppliedOn() { return appliedOn; }
    public void setAppliedOn(LocalDate appliedOn) { this.appliedOn = appliedOn; }

    public Employee getApprover() { return approver; }
    public void setApprover(Employee approver) { this.approver = approver; }

    public LocalDate getDecidedOn() { return decidedOn; }
    public void setDecidedOn(LocalDate decidedOn) { this.decidedOn = decidedOn; }

    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
}
