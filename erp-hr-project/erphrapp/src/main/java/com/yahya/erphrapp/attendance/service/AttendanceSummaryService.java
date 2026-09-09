package com.yahya.erphrapp.attendance.service;

import com.yahya.erphrapp.attendance.dto.AttendanceSummaryResponse;
import com.yahya.erphrapp.attendance.entity.AttendanceSummary;
import com.yahya.erphrapp.attendance.mapper.AttendanceSummaryMapper;
import com.yahya.erphrapp.attendance.repository.AttendanceSummaryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceSummaryService {
    private final AttendanceSummaryMapper attendanceSummaryMapper;
    private final AttendanceSummaryRepository attendanceSummaryRepository;

    public AttendanceSummaryService(AttendanceSummaryMapper attendanceSummaryMapper, AttendanceSummaryRepository attendanceSummaryRepository) {
        this.attendanceSummaryMapper = attendanceSummaryMapper;
        this.attendanceSummaryRepository = attendanceSummaryRepository;
    }

    // read all attendances
    public List<AttendanceSummaryResponse> getAttendances() {
        List<AttendanceSummary> attendanceSummaries = attendanceSummaryRepository.findAll();
        return  attendanceSummaries.stream().map(attendanceSummaryMapper::toResponse).toList();
    }

    // read attendances for one employee
    public List<AttendanceSummaryResponse> getAttendancesByEmployeeId(Long empId) {
        List<AttendanceSummary> attendanceSummaries = attendanceSummaryRepository.findAllByEmployeeId(empId);
        return  attendanceSummaries.stream().map(attendanceSummaryMapper::toResponse).toList();
    }
}
