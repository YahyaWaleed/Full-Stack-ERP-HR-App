package com.yahya.erphrapp.leaves.repository;

import com.yahya.erphrapp.leaves.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    List<LeaveBalance> findAllByEmployeeId(Long empId);

    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndFiscalYear(Long empId, Long typeId, int fiscalYear);
}