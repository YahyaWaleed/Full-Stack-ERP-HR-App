package com.yahya.erphrapp.leaves.controller;

import com.yahya.erphrapp.leaves.dto.LeaveBalanceResponse;
import com.yahya.erphrapp.leaves.service.LeaveBalanceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;

    public LeaveBalanceController(LeaveBalanceService leaveBalanceService) {
        this.leaveBalanceService = leaveBalanceService;
    }

    // read one balance by its own ID
    @GetMapping("/leave-balances/{id}")
    public LeaveBalanceResponse getLeaveBalance(@PathVariable Long id) {
        return leaveBalanceService.getLeaveBalance(id);
    }

    // read all balances for one employee
    @GetMapping("/employees/{empId}/leave-balances")
    public List<LeaveBalanceResponse> getLeaveBalancesForEmployee(@PathVariable Long empId) {
        return leaveBalanceService.getLeaveBalancesForEmployee(empId);
    }
}