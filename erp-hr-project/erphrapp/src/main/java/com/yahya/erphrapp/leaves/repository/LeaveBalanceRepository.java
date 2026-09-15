package com.yahya.erphrapp.leaves.repository;

import com.yahya.erphrapp.leaves.entity.LeaveBalance;
import com.yahya.erphrapp.leaves.entity.LeaveRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    @EntityGraph(attributePaths = {"employee","leaveType"})
    List<LeaveBalance> findAllByEmployeeId(Long empId);

    @EntityGraph(attributePaths = {"employee","leaveType"})
    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndFiscalYear(Long empId, Long typeId, int fiscalYear);
}