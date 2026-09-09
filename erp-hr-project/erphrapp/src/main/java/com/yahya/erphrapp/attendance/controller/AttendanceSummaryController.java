package com.yahya.erphrapp.attendance.controller;

import com.yahya.erphrapp.attendance.dto.AttendanceSummaryResponse;
import com.yahya.erphrapp.attendance.entity.AttendanceSummary;
import com.yahya.erphrapp.attendance.service.AttendanceSummaryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AttendanceSummaryController {
    private final AttendanceSummaryService attendanceSummaryService;

    public AttendanceSummaryController(AttendanceSummaryService attendanceSummaryService) {
        this.attendanceSummaryService = attendanceSummaryService;
    }

    // read all attendances
    @GetMapping("/attendance")
    public List<AttendanceSummaryResponse> getAttendances() {
        return attendanceSummaryService.getAttendances();
    }

    // read attendances for one employee
    @GetMapping("/employees/{empId}/attendance")
    public List<AttendanceSummaryResponse> getAttendancesByEmployeeId(@PathVariable Long empId) {
        return attendanceSummaryService.getAttendancesByEmployeeId(empId);
    }
}
