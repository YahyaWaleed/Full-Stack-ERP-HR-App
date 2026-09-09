package com.yahya.erphrapp.leaves.repository;

import com.yahya.erphrapp.leaves.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {


    List<LeaveRequest> findAllByEmployeeId(Long employee_id);
}
