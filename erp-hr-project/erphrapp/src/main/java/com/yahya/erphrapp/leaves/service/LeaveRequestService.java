package com.yahya.erphrapp.leaves.service;

import com.yahya.erphrapp.audit.AuditService;
import com.yahya.erphrapp.employee.entity.Employee;
import com.yahya.erphrapp.employee.repository.EmployeeRepository;
import com.yahya.erphrapp.exception.BadRequestException;
import com.yahya.erphrapp.exception.ConflictException;
import com.yahya.erphrapp.exception.ResourceNotFoundException;
import com.yahya.erphrapp.leaves.dto.LeaveRequestRequest;
import com.yahya.erphrapp.leaves.dto.LeaveRequestResponse;
import com.yahya.erphrapp.leaves.dto.RejectLeaveRequest;
import com.yahya.erphrapp.leaves.entity.LeaveBalance;
import com.yahya.erphrapp.leaves.entity.LeaveRequest;
import com.yahya.erphrapp.leaves.entity.LeaveType;
import com.yahya.erphrapp.leaves.mapper.LeaveRequestMapper;
import com.yahya.erphrapp.leaves.repository.LeaveBalanceRepository;
import com.yahya.erphrapp.leaves.repository.LeaveRequestRepository;
import com.yahya.erphrapp.leaves.repository.LeaveTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class LeaveRequestService {

    private final LeaveRequestMapper leaveRequestMapper;
    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final WorkingDayCalculator workingDayCalculator;
    private final AuditService auditService;

    public LeaveRequestService(LeaveBalanceRepository leaveBalanceRepository, LeaveTypeRepository leaveTypeRepository,
                               EmployeeRepository employeeRepository, LeaveRequestMapper leaveRequestMapper,
                               LeaveRequestRepository leaveRequestRepository, WorkingDayCalculator workingDayCalculator,
                               AuditService auditService) {
        this.leaveRequestMapper = leaveRequestMapper;
        this.employeeRepository = employeeRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.workingDayCalculator = workingDayCalculator;
        this.auditService = auditService;
    }

    // read one leave request by its request id
    @Transactional(readOnly = true)
    public LeaveRequestResponse getLeaveRequest(Long id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Leave Request", id));
        return leaveRequestMapper.toResponse(leaveRequest);
    }

    // read all leave requests
    @Transactional(readOnly = true)
    public Page<LeaveRequestResponse> getRequests(Pageable pageable) {
        return leaveRequestRepository.findAllBy(pageable)
                .map(leaveRequestMapper::toResponse);
    }

    // read all leave requests by employee ID
    @Transactional(readOnly = true)
    public Page<LeaveRequestResponse> getRequestsByEmployeeId(Long empId, Pageable pageable) {
        return leaveRequestRepository.findAllByEmployeeId(empId, pageable)
                .map(leaveRequestMapper::toResponse);
    }

    // create a leave request, enforcing the leave type's rules (review 10.3)
    @Transactional
    public LeaveRequestResponse createLeaveRequest(Long empId, LeaveRequestRequest request) {
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", empId));
        LeaveType leaveType = leaveTypeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave Type", request.getTypeId()));

        if (employee.isTerminated()) {
            throw new ConflictException("Cannot request leave for a terminated employee");
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date must be on or after the start date");
        }
        if (!LeaveBalanceService.appliesTo(leaveType.getGenderRestriction().name(), employee.getGender())) {
            throw new BadRequestException(leaveType.getNameEn() + " does not apply to this employee");
        }

        // weekends and public holidays don't count against the balance
        int workingDays = workingDayCalculator.workingDaysBetween(request.getStartDate(), request.getEndDate());
        if (workingDays == 0) {
            throw new BadRequestException("The selected dates contain no working days");
        }
        if (leaveType.getMaxConsecutive() > 0 && workingDays > leaveType.getMaxConsecutive()) {
            throw new BadRequestException(leaveType.getNameEn() + " allows at most " + leaveType.getMaxConsecutive()
                    + " consecutive working days (requested " + workingDays + ")");
        }
        if (leaveType.isRequiresAttachment() && (request.getAttachmentRef() == null || request.getAttachmentRef().isBlank())) {
            throw new BadRequestException(leaveType.getNameEn() + " requires a supporting document (attachmentRef)");
        }
        if (leaveRequestRepository.countOverlapping(empId, request.getStartDate(), request.getEndDate()) > 0) {
            throw new ConflictException("These dates overlap another pending or approved leave for this employee");
        }

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setStartDate(request.getStartDate());
        leaveRequest.setEndDate(request.getEndDate());
        leaveRequest.setReason(request.getReason());
        leaveRequest.setAttachmentRef(request.getAttachmentRef());
        leaveRequest.setStatus(LeaveRequest.LeaveStatus.PENDING);
        leaveRequest.setAppliedOn(LocalDate.now());
        leaveRequest.setDaysCount(BigDecimal.valueOf(workingDays));

        leaveRequestRepository.save(leaveRequest);
        return leaveRequestMapper.toResponse(leaveRequest);
    }

    // cancel a leave request
    @Transactional
    public void cancelRequest(Long id) {
        LeaveRequest leaveRequest = lock(id);
        leaveRequest.cancel(LocalDate.now());
        leaveRequestRepository.save(leaveRequest);
        auditService.record("LEAVE_CANCELLED", "LEAVE_REQUEST", id, describe(leaveRequest));
    }

    // approve a leave request. The request and the balance row are both locked, so two approvals can't
    // both pass the balance check and overdraw it (review 7.9); trg_leave_approved deducts in this transaction.
    @Transactional
    public void approveRequest(Long id) {
        LeaveRequest leaveRequest = lock(id);
        if (!leaveRequest.isPending()) {
            leaveRequest.approve(LocalDate.now()); // throws the "only PENDING" conflict
        }

        LeaveType type = leaveRequest.getLeaveType();
        if (type.isAffectsBalance()) {
            Long empId = leaveRequest.getEmployee().getId();
            int fiscalYear = leaveRequest.getStartDate().getYear();
            LeaveBalance balance = leaveBalanceRepository.findForUpdate(empId, type.getId(), fiscalYear)
                    .orElseThrow(() -> new ConflictException("Employee has no " + type.getNameEn() + " balance for " + fiscalYear));
            if (balance.getRemainingDays().compareTo(leaveRequest.getDaysCount()) < 0) {
                throw new ConflictException("Employee does not have enough remaining leave balance for this request ("
                        + balance.getRemainingDays() + " left, " + leaveRequest.getDaysCount() + " requested)");
            }
        }

        leaveRequest.approve(LocalDate.now());
        leaveRequestRepository.saveAndFlush(leaveRequest);
        auditService.record("LEAVE_APPROVED", "LEAVE_REQUEST", id, describe(leaveRequest));
    }

    // reject a leave request
    @Transactional
    public void rejectRequest(Long id, RejectLeaveRequest rejectLeaveRequest) {
        LeaveRequest leaveRequest = lock(id);
        leaveRequest.reject(rejectLeaveRequest.getRejectReason(), LocalDate.now());
        leaveRequestRepository.save(leaveRequest);
        auditService.record("LEAVE_REJECTED", "LEAVE_REQUEST", id, describe(leaveRequest) + " reason=" + rejectLeaveRequest.getRejectReason());
    }

    private LeaveRequest lock(Long id) {
        return leaveRequestRepository.findByIdForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("Leave Request", id));
    }

    private static String describe(LeaveRequest r) {
        return r.getEmployee().getEmpCode() + " " + r.getLeaveType().getCode() + " " + r.getStartDate() + ".." + r.getEndDate()
                + " (" + r.getDaysCount() + " days)";
    }
}
