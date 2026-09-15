package com.yahya.erphrapp.leaves.repository;

import com.yahya.erphrapp.leaves.entity.LeaveRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    @EntityGraph(attributePaths = {"employee", "leaveType", "approver"})
    Page<LeaveRequest> findAllBy(Pageable pageable);

    @EntityGraph(attributePaths = {"employee", "leaveType", "approver"})
    Page<LeaveRequest> findAllByEmployeeId(Long empId, Pageable pageable);

    long countByStatus(LeaveRequest.LeaveStatus status);

    @EntityGraph(attributePaths = {"employee", "leaveType", "approver"})
    List<LeaveRequest> findAllByEmployeeIdAndStatus(Long employeeId, LeaveRequest.LeaveStatus status);
}
