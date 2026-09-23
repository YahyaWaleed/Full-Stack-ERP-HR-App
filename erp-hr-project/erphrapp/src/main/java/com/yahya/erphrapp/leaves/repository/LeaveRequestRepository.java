package com.yahya.erphrapp.leaves.repository;

import com.yahya.erphrapp.leaves.entity.LeaveRequest;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    @EntityGraph(attributePaths = {"employee", "leaveType", "approver"})
    Page<LeaveRequest> findAllBy(Pageable pageable);

    @EntityGraph(attributePaths = {"employee", "leaveType", "approver"})
    Page<LeaveRequest> findAllByEmployeeId(Long empId, Pageable pageable);

    @EntityGraph(attributePaths = {"employee", "leaveType", "approver"})
    Optional<LeaveRequest> findById(Long id);

    List<LeaveRequest> findAllByEmployeeId(Long empId);

    long countByStatus(LeaveRequest.LeaveStatus status);

    // SELECT ... FOR UPDATE: a second approval of the same request waits, then sees it is no longer PENDING
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from LeaveRequest r join fetch r.leaveType join fetch r.employee where r.id = :id")
    Optional<LeaveRequest> findByIdForUpdate(Long id);

    // pending or approved leave of this employee that overlaps [start, end]
    @Query("""
            select count(r) from LeaveRequest r
             where r.employee.id = :empId
               and r.status in (com.yahya.erphrapp.leaves.entity.LeaveRequest.LeaveStatus.PENDING,
                                com.yahya.erphrapp.leaves.entity.LeaveRequest.LeaveStatus.APPROVED)
               and r.startDate <= :end and r.endDate >= :start""")
    long countOverlapping(Long empId, LocalDate start, LocalDate end);
}
