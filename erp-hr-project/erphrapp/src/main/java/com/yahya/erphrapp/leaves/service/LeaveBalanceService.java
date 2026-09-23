package com.yahya.erphrapp.leaves.service;

import com.yahya.erphrapp.employee.entity.Employee;
import com.yahya.erphrapp.exception.ResourceNotFoundException;
import com.yahya.erphrapp.leaves.dto.LeaveBalanceResponse;
import com.yahya.erphrapp.leaves.dto.LeaveTypeResponse;
import com.yahya.erphrapp.leaves.entity.LeaveBalance;
import com.yahya.erphrapp.leaves.mapper.LeaveBalanceMapper;
import com.yahya.erphrapp.leaves.repository.LeaveBalanceRepository;
import com.yahya.erphrapp.leaves.repository.LeaveTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveBalanceMapper leaveBalanceMapper;
    private final LeaveTypeService leaveTypeService;
    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveBalanceService(LeaveBalanceRepository leaveBalanceRepository, LeaveBalanceMapper leaveBalanceMapper,
                               LeaveTypeService leaveTypeService, LeaveTypeRepository leaveTypeRepository) {
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.leaveBalanceMapper = leaveBalanceMapper;
        this.leaveTypeService = leaveTypeService;
        this.leaveTypeRepository = leaveTypeRepository;
    }

    // read one balance by its own ID
    @Transactional(readOnly = true)
    public LeaveBalanceResponse getLeaveBalance(Long id) {
        LeaveBalance balance = leaveBalanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave Balance", id));
        return leaveBalanceMapper.toResponse(balance);
    }

    // read all balances for one employee
    @Transactional(readOnly = true)
    public List<LeaveBalanceResponse> getLeaveBalancesForEmployee(Long empId) {
        return leaveBalanceRepository.findAllByEmployeeId(empId)
                .stream()
                .map(leaveBalanceMapper::toResponse)
                .toList();
    }

    // opens a balance for every leave type that tracks one and applies to the employee's gender.
    // Annual leave uses the contract's entitlement; everything else uses the type's yearly quota.
    @Transactional
    public void initializeFor(Employee employee, int fiscalYear, int annualLeaveDays) {
        for (LeaveTypeResponse type : leaveTypeService.getLeaveTypes()) { // cached reference data
            if (!type.isAffectsBalance() || !appliesTo(type.getGenderRestriction(), employee.getGender())) {
                continue;
            }
            LeaveBalance balance = new LeaveBalance();
            balance.setEmployee(employee);
            balance.setLeaveType(leaveTypeRepository.getReferenceById(type.getId()));
            balance.setFiscalYear(fiscalYear);
            balance.setEntitledDays(BigDecimal.valueOf("ANN".equals(type.getCode()) ? annualLeaveDays : type.getAnnualQuota()));
            balance.setCarriedForward(BigDecimal.ZERO);
            balance.setUsedDays(BigDecimal.ZERO);
            leaveBalanceRepository.save(balance);
        }
    }

    // the enum allows both M/F and MALE/FEMALE spellings
    static boolean appliesTo(String restriction, Employee.Gender gender) {
        if (restriction == null || "ANY".equals(restriction)) {
            return true;
        }
        return switch (gender) {
            case M -> "M".equals(restriction) || "MALE".equals(restriction);
            case F -> "F".equals(restriction) || "FEMALE".equals(restriction);
        };
    }
}
