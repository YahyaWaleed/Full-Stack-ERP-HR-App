package com.yahya.erphrapp.leaves.service;

import com.yahya.erphrapp.exception.ResourceNotFoundException;
import com.yahya.erphrapp.leaves.dto.LeaveTypeResponse;
import com.yahya.erphrapp.leaves.entity.LeaveType;
import com.yahya.erphrapp.leaves.mapper.LeaveTypeMapper;
import com.yahya.erphrapp.leaves.repository.LeaveTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveTypeMapper leaveTypeMapper;

    public LeaveTypeService(LeaveTypeRepository leaveTypeRepository, LeaveTypeMapper leaveTypeMapper) {
        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveTypeMapper = leaveTypeMapper;
    }

    // read all leave types
    public List<LeaveTypeResponse> getLeaveTypes() {
        return leaveTypeRepository.findAll()
                .stream()
                .map(leaveTypeMapper::toResponse)
                .toList();
    }

    // read one leave type by its own ID
    public LeaveTypeResponse getLeaveType(Long id) {
        LeaveType leaveType = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave Type", id));
        return leaveTypeMapper.toResponse(leaveType);
    }
}