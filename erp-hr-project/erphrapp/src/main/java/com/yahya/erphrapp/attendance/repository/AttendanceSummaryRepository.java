package com.yahya.erphrapp.attendance.repository;

import com.yahya.erphrapp.attendance.entity.AttendanceSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceSummaryRepository extends JpaRepository<AttendanceSummary,Integer> {
    List<AttendanceSummary> findAllByEmployeeId(Long employee_id);
}
