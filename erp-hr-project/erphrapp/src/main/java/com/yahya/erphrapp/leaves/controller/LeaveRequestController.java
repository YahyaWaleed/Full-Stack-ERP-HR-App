package com.yahya.erphrapp.leaves.controller;

import com.yahya.erphrapp.exception.ConflictException;
import com.yahya.erphrapp.exception.ResourceNotFoundException;
import com.yahya.erphrapp.leaves.dto.LeaveRequestRequest;
import com.yahya.erphrapp.leaves.dto.LeaveRequestResponse;
import com.yahya.erphrapp.leaves.dto.RejectLeaveRequest;
import com.yahya.erphrapp.leaves.entity.LeaveRequest;
import com.yahya.erphrapp.leaves.service.LeaveRequestService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LeaveRequestController {
    private final LeaveRequestService leaveRequestService;
    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    // read one leave request by its request id
    @GetMapping("/leaves/{requestId}")
    public LeaveRequestResponse getLeaveRequest(@PathVariable Long requestId) {
        return leaveRequestService.getLeaveRequest(requestId);
    }

    // read all leave requests
    @GetMapping("/leaves")
    public List<LeaveRequestResponse> getLeaveRequests() {
        return leaveRequestService.getLeaveRequests();
    }

    // read leave requests for an employee by employee ID
    @GetMapping("/employees/{empId}/leaves")
    public List<LeaveRequestResponse> getLeaveRequestsByEmployeeId(@PathVariable Long empId) {
        return leaveRequestService.getLeaveRequestsByEmployeeId(empId);
    }


    // create a leave  request for an employee by emp ID
    @PostMapping("/employees/{empId}/leaves")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public LeaveRequestResponse createLeaveRequest(@PathVariable Long empId, @RequestBody LeaveRequestRequest leaveRequestRequest) {
        return leaveRequestService.createLeaveRequest(empId, leaveRequestRequest);
    }

    // cancel a leave request
    @PostMapping("/leaves/{requestId}/cancel")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public void cancelRequest(@PathVariable Long requestId) {
        leaveRequestService.cancelRequest(requestId);
    }

    // approve a leave request
    @PostMapping("/leaves/{requestId}/approve")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public void approveRequest(@PathVariable Long requestId) {
        leaveRequestService.approveRequest(requestId);
    }

    // reject a leave request
    @PostMapping("/leaves/{requestId}/reject")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public void rejectRequest(@PathVariable Long requestId, @RequestBody RejectLeaveRequest rejectLeaveRequest) {
        leaveRequestService.rejectRequest(requestId,rejectLeaveRequest);
    }
}
