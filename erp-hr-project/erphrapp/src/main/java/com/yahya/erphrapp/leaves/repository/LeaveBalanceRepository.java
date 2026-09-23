package com.yahya.erphrapp.leaves.repository;

import com.yahya.erphrapp.leaves.entity.LeaveBalance;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    @EntityGraph(attributePaths = {"employee","leaveType"})
    List<LeaveBalance> findAllByEmployeeId(Long empId);

    @EntityGraph(attributePaths = {"employee","leaveType"})
    Optional<LeaveBalance> findById(Long id);

    // locked read used by approval: concurrent approvals against the same balance run one after the other,
    // and each sees the days the previous one used (the trigger deducts inside the same transaction)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select b from LeaveBalance b
             where b.employee.id = :empId and b.leaveType.id = :typeId and b.fiscalYear = :fiscalYear""")
    Optional<LeaveBalance> findForUpdate(Long empId, Long typeId, int fiscalYear);
}
