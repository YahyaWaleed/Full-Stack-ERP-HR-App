package com.yahya.erphrapp.leaves.repository;

import com.yahya.erphrapp.leaves.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {

    List<LeaveType> findAll();
}