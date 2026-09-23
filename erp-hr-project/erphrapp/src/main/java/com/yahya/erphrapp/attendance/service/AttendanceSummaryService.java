package com.yahya.erphrapp.attendance.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional(readOnly = true)
    public Page<AttendanceSummaryResponse> getAttendances(String periodCode, Pageable pageable) {
        return (periodCode == null
                ? attendanceSummaryRepository.findAllBy(pageable)
                : attendanceSummaryRepository.findAllByPayrollPeriodPeriodCode(periodCode, pageable))
                .map(attendanceSummaryMapper::toResponse);
    }

    // read attendances for one employee
    @Transactional(readOnly = true)
    public List<AttendanceSummaryResponse> getAttendancesByEmployeeId(Long empId) {
        List<AttendanceSummary> attendanceSummaries = attendanceSummaryRepository.findAllByEmployeeIdOrderByPayrollPeriodPeriodCodeDesc(empId);
        return  attendanceSummaries.stream().map(attendanceSummaryMapper::toResponse).toList();
    }
}
