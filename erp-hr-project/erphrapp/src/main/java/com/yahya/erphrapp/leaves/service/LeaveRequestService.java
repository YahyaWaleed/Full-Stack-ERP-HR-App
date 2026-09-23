package com.yahya.erphrapp.leaves.service;

import com.yahya.erphrapp.employee.entity.Employee;
import com.yahya.erphrapp.employee.repository.EmployeeRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LeaveRequestService {
    // inject mapper and repo
    private final LeaveRequestMapper leaveRequestMapper;
    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;

    public LeaveRequestService(LeaveBalanceRepository leaveBalanceRepository, LeaveTypeRepository leaveTypeRepository, EmployeeRepository employeeRepository, LeaveRequestMapper leaveRequestMapper, LeaveRequestRepository leaveRequestRepository) {
        this.leaveRequestMapper = leaveRequestMapper;
        this.employeeRepository = employeeRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
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

    // create a leave request for an employee by emp ID
    @Transactional
    public LeaveRequestResponse createLeaveRequest(Long empId, LeaveRequestRequest leaveRequestRequest) {

        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", empId));

        LeaveType leaveType = leaveTypeRepository.findById(leaveRequestRequest.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave Type", leaveRequestRequest.getTypeId()));

        if (leaveRequestRequest.getEndDate().isBefore(leaveRequestRequest.getStartDate())) {
            throw new ConflictException("End date must be on or after the start date");
        }

        LeaveRequest leaveRequest = new LeaveRequest();

        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setStartDate(leaveRequestRequest.getStartDate());
        leaveRequest.setEndDate(leaveRequestRequest.getEndDate());
        leaveRequest.setReason(leaveRequestRequest.getReason());
        leaveRequest.setStatus(LeaveRequest.LeaveStatus.PENDING);
        leaveRequest.setAppliedOn(LocalDate.now());

        long workingDays = calculateWorkingDays(leaveRequestRequest.getStartDate(), leaveRequestRequest.getEndDate());
        leaveRequest.setDaysCount(BigDecimal.valueOf(workingDays));

        leaveRequestRepository.save(leaveRequest);

        return leaveRequestMapper.toResponse(leaveRequest);
    }

    // counts working days in the range, excluding Friday and Saturday
    private long calculateWorkingDays(LocalDate start, LocalDate end) {
        long count = 0;
        LocalDate current = start;
        while (!current.isAfter(end)) {
            DayOfWeek day = current.getDayOfWeek();
            if (day != DayOfWeek.FRIDAY && day != DayOfWeek.SATURDAY) {
                count++;
            }
            current = current.plusDays(1);
        }
        return count;
    }

    // cancel a leave request
    @Transactional
    public void cancelRequest(Long id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Leave Request", id));
        if (leaveRequest.getStatus() != LeaveRequest.LeaveStatus.PENDING) {
            throw new ConflictException("Leave request status must be set to PENDING before cancelling it");
        }
        leaveRequest.setStatus(LeaveRequest.LeaveStatus.CANCELLED);
        leaveRequestRepository.save(leaveRequest);
    }

    // approve a leave request
    @Transactional
    public void approveRequest(Long id) {

        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave Request", id));

        if (leaveRequest.getStatus() != LeaveRequest.LeaveStatus.PENDING) {
            throw new ConflictException("Leave request status must be PENDING before approving it.");
        }

        Long empId = leaveRequest.getEmployee().getId();
        Long typeId = leaveRequest.getLeaveType().getId();
        int fiscalYear = leaveRequest.getStartDate().getYear();

        // Only check leave balance if this leave type affects the employee's balance
        if (leaveRequest.getLeaveType().isAffectsBalance()) {

            LeaveBalance balance = leaveBalanceRepository
                    .findByEmployeeIdAndLeaveTypeIdAndFiscalYear(empId, typeId, fiscalYear)
                    .orElseThrow(() -> new ResourceNotFoundException("Leave Balance", empId));

            if (balance.getRemainingDays().compareTo(leaveRequest.getDaysCount()) < 0) {
                throw new ConflictException(
                        "Employee does not have enough remaining leave balance for this request"
                );
            }
        }

        // Approve the leave request
        leaveRequest.setStatus(LeaveRequest.LeaveStatus.APPROVED);
        leaveRequest.setDecidedOn(LocalDate.now());

        leaveRequestRepository.save(leaveRequest);
    }

    // reject a leave request
    @Transactional
    public void rejectRequest(Long id, RejectLeaveRequest rejectLeaveRequest) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Leave Request not found", id));
        if (leaveRequest.getStatus() != LeaveRequest.LeaveStatus.PENDING) {
            throw new ConflictException("Leave request status must be set to PENDING before rejecting it");
        }
        leaveRequest.setStatus(LeaveRequest.LeaveStatus.REJECTED);
        leaveRequest.setRejectReason(rejectLeaveRequest.getRejectReason());
        leaveRequest.setDecidedOn(LocalDate.now());
        leaveRequestRepository.save(leaveRequest);
    }
}
