package com.yahya.erphrapp.attendance.repository;

import com.yahya.erphrapp.attendance.entity.AttendanceSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceSummaryRepository extends JpaRepository<AttendanceSummary,Integer> {

    @EntityGraph(attributePaths = {"employee", "payrollPeriod"})
    Page<AttendanceSummary> findAllBy(Pageable pageable);

    @EntityGraph(attributePaths = {"employee", "payrollPeriod"})
    Page<AttendanceSummary> findAllByPayrollPeriodPeriodCode(String periodCode, Pageable pageable);

    @EntityGraph(attributePaths = {"employee", "payrollPeriod"})
    List<AttendanceSummary> findAllByEmployeeIdOrderByPayrollPeriodPeriodCodeDesc(Long empId);
}
