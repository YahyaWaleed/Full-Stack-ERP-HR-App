package com.yahya.erphrapp.leaves.controller;

import com.yahya.erphrapp.leaves.dto.LeaveTypeResponse;
import com.yahya.erphrapp.leaves.service.LeaveTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leave-types")
public class LeaveTypeController {

    private final LeaveTypeService leaveTypeService;

    public LeaveTypeController(LeaveTypeService leaveTypeService) {
        this.leaveTypeService = leaveTypeService;
    }

    // read all leave types
    @GetMapping
    public List<LeaveTypeResponse> getLeaveTypes() {
        return leaveTypeService.getLeaveTypes();
    }

    // read one leave type by its own ID
    @GetMapping("/{id}")
    public LeaveTypeResponse getLeaveType(@PathVariable Long id) {
        return leaveTypeService.getLeaveType(id);
    }
}