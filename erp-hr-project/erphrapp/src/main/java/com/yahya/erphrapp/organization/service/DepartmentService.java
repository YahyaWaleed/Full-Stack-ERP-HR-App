package com.yahya.erphrapp.organization.service;

import com.yahya.erphrapp.exception.ResourceNotFoundException;
import com.yahya.erphrapp.organization.dto.DepartmentResponse;
import com.yahya.erphrapp.organization.entity.Department;
import com.yahya.erphrapp.organization.mapper.DepartmentMapper;
import com.yahya.erphrapp.organization.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {
    // inject stuff first
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    public DepartmentService(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
    }

    // get all departments
    public List<DepartmentResponse> getDepartments() {
        List<DepartmentResponse> departments = departmentRepository.findAll().stream().map(departmentMapper::toResponse).toList();
        return departments;
    }

    // get one department
    public DepartmentResponse getDepartment(Long departmentId) {
        return departmentMapper.toResponse(departmentRepository.findById(departmentId).orElseThrow(() -> new ResourceNotFoundException("Department",departmentId)));
    }
}
