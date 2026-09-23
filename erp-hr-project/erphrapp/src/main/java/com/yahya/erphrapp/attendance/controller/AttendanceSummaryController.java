package com.yahya.erphrapp.attendance.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestParam;
import com.yahya.erphrapp.attendance.dto.AttendanceSummaryResponse;
import com.yahya.erphrapp.attendance.service.AttendanceSummaryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AttendanceSummaryController {
    private final AttendanceSummaryService attendanceSummaryService;

    public AttendanceSummaryController(AttendanceSummaryService attendanceSummaryService) {
        this.attendanceSummaryService = attendanceSummaryService;
    }

    // read all attendances
    // newest records first by primary key: sorting on the joined period code made MySQL sort the whole table per page
    @GetMapping("/attendance")
    public Page<AttendanceSummaryResponse> getAttendances(@RequestParam(required = false) String periodCode,
                                                          @PageableDefault(size = 25, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return attendanceSummaryService.getAttendances(periodCode, pageable);
    }

    // read attendances for one employee
    @GetMapping("/employees/{empId}/attendance")
    public List<AttendanceSummaryResponse> getAttendancesByEmployeeId(@PathVariable Long empId) {
        return attendanceSummaryService.getAttendancesByEmployeeId(empId);
    }
}
