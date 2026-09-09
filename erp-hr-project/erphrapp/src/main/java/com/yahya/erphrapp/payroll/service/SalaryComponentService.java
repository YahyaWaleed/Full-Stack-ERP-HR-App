package com.yahya.erphrapp.payroll.service;

import com.yahya.erphrapp.exception.ResourceNotFoundException;
import com.yahya.erphrapp.payroll.dto.SalaryComponentResponse;
import com.yahya.erphrapp.payroll.entity.SalaryComponent;
import com.yahya.erphrapp.payroll.mapper.SalaryComponentMapper;
import com.yahya.erphrapp.payroll.repository.SalaryComponentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaryComponentService {

    private final SalaryComponentRepository salaryComponentRepository;
    private final SalaryComponentMapper salaryComponentMapper;

    public SalaryComponentService(SalaryComponentRepository salaryComponentRepository,
                                  SalaryComponentMapper salaryComponentMapper) {
        this.salaryComponentRepository = salaryComponentRepository;
        this.salaryComponentMapper = salaryComponentMapper;
    }

    // read all salary components (the catalog)
    public List<SalaryComponentResponse> getComponents() {
        return salaryComponentRepository.findAll()
                .stream()
                .map(salaryComponentMapper::toResponse)
                .toList();
    }

    // read one salary component by its own ID
    public SalaryComponentResponse getComponent(Long id) {
        SalaryComponent component = salaryComponentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary Component", id));
        return salaryComponentMapper.toResponse(component);
    }
}