package com.yahya.erphrapp.leaves.controller;

import com.yahya.erphrapp.leaves.dto.LeaveRequestRequest;
import com.yahya.erphrapp.leaves.dto.LeaveRequestResponse;
import com.yahya.erphrapp.leaves.dto.RejectLeaveRequest;
import com.yahya.erphrapp.leaves.service.LeaveRequestService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
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
    public Page<LeaveRequestResponse> getRequests(@PageableDefault(size = 25, sort = "appliedOn") Pageable pageable) {
        return leaveRequestService.getRequests(pageable);
    }

    // read all leave requests for one employee
    @GetMapping("/employees/{empId}/leaves")
    public Page<LeaveRequestResponse> getRequestsByEmployeeId(@PathVariable Long empId,
                                                              @PageableDefault(size = 25, sort = "startDate") Pageable pageable) {
        return leaveRequestService.getRequestsByEmployeeId(empId, pageable);
    }

    // create a leave request for an employee
    @PostMapping("/employees/{empId}/leaves")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public LeaveRequestResponse createLeaveRequest(@PathVariable Long empId, @Valid @RequestBody LeaveRequestRequest leaveRequestRequest) {
        return leaveRequestService.createLeaveRequest(empId, leaveRequestRequest);
    }

    @PostMapping("/leaves/{requestId}/cancel")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public void cancelRequest(@PathVariable Long requestId) {
        leaveRequestService.cancelRequest(requestId);
    }

    @PostMapping("/leaves/{requestId}/approve")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public void approveRequest(@PathVariable Long requestId) {
        leaveRequestService.approveRequest(requestId);
    }

    @PostMapping("/leaves/{requestId}/reject")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public void rejectRequest(@PathVariable Long requestId, @Valid @RequestBody RejectLeaveRequest rejectLeaveRequest) {
        leaveRequestService.rejectRequest(requestId, rejectLeaveRequest);
    }
}
