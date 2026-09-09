package com.yahya.erphrapp.leaves.service;

import com.yahya.erphrapp.exception.ResourceNotFoundException;
import com.yahya.erphrapp.leaves.dto.LeaveBalanceResponse;
import com.yahya.erphrapp.leaves.entity.LeaveBalance;
import com.yahya.erphrapp.leaves.mapper.LeaveBalanceMapper;
import com.yahya.erphrapp.leaves.repository.LeaveBalanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveBalanceMapper leaveBalanceMapper;

    public LeaveBalanceService(LeaveBalanceRepository leaveBalanceRepository, LeaveBalanceMapper leaveBalanceMapper) {
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.leaveBalanceMapper = leaveBalanceMapper;
    }

    // read one balance by its own ID
    public LeaveBalanceResponse getLeaveBalance(Long id) {
        LeaveBalance balance = leaveBalanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave Balance", id));
        return leaveBalanceMapper.toResponse(balance);
    }

    // read all balances for one employee
    public List<LeaveBalanceResponse> getLeaveBalancesForEmployee(Long empId) {
        return leaveBalanceRepository.findAllByEmployeeId(empId)
                .stream()
                .map(leaveBalanceMapper::toResponse)
                .toList();
    }
}