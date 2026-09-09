package com.yahya.erphrapp.employee.service;

import com.yahya.erphrapp.employee.dto.EmployeeSalaryComponentRequest;
import com.yahya.erphrapp.employee.dto.EmployeeSalaryComponentResponse;
import com.yahya.erphrapp.employee.entity.Employee;
import com.yahya.erphrapp.employee.entity.EmployeeSalaryComponent;
import com.yahya.erphrapp.employee.mapper.EmployeeSalaryComponentMapper;
import com.yahya.erphrapp.employee.repository.EmployeeRepository;
import com.yahya.erphrapp.employee.repository.EmployeeSalaryComponentRepository;
import com.yahya.erphrapp.exception.ResourceNotFoundException;
import com.yahya.erphrapp.payroll.entity.SalaryComponent;
import com.yahya.erphrapp.payroll.repository.SalaryComponentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeSalaryComponentService {

    private final EmployeeSalaryComponentRepository employeeSalaryComponentRepository;
    private final EmployeeSalaryComponentMapper employeeSalaryComponentMapper;
    private final EmployeeRepository employeeRepository;
    private final SalaryComponentRepository salaryComponentRepository;

    public EmployeeSalaryComponentService(EmployeeSalaryComponentRepository employeeSalaryComponentRepository,
                                          EmployeeSalaryComponentMapper employeeSalaryComponentMapper,
                                          EmployeeRepository employeeRepository,
                                          SalaryComponentRepository salaryComponentRepository) {
        this.employeeSalaryComponentRepository = employeeSalaryComponentRepository;
        this.employeeSalaryComponentMapper = employeeSalaryComponentMapper;
        this.employeeRepository = employeeRepository;
        this.salaryComponentRepository = salaryComponentRepository;
    }

    // read all salary components for one employee
    public List<EmployeeSalaryComponentResponse> getComponentsForEmployee(Long empId) {
        return employeeSalaryComponentRepository.findByEmployeeId(empId)
                .stream()
                .map(employeeSalaryComponentMapper::toResponse)
                .toList();
    }

    // create a salary component for an employee
    @Transactional
    public EmployeeSalaryComponentResponse createComponent(Long empId, EmployeeSalaryComponentRequest request) {
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", empId));

        SalaryComponent salaryComponent = salaryComponentRepository.findById(request.getCompId())
                .orElseThrow(() -> new ResourceNotFoundException("Salary Component", request.getCompId()));

        EmployeeSalaryComponent component = employeeSalaryComponentMapper.toEntity(request);
        component.setEmployee(employee);
        component.setSalaryComponent(salaryComponent);

        employeeSalaryComponentRepository.save(component);
        return employeeSalaryComponentMapper.toResponse(component);
    }

    // update a salary component
    @Transactional
    public EmployeeSalaryComponentResponse updateComponent(Long id, EmployeeSalaryComponentRequest request) {
        EmployeeSalaryComponent component = employeeSalaryComponentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee Salary Component", id));

        SalaryComponent salaryComponent = salaryComponentRepository.findById(request.getCompId())
                .orElseThrow(() -> new ResourceNotFoundException("Salary Component", request.getCompId()));

        component.setSalaryComponent(salaryComponent);
        component.setAmount(request.getAmount());
        component.setPercentage(request.getPercentage());
        component.setEffectiveFrom(request.getEffectiveFrom());
        component.setEffectiveTo(request.getEffectiveTo());
        component.setNotes(request.getNotes());

        employeeSalaryComponentRepository.save(component);
        return employeeSalaryComponentMapper.toResponse(component);
    }

    // delete a salary component
    public void deleteComponent(Long id) {
        if (!employeeSalaryComponentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee Salary Component", id);
        }
        employeeSalaryComponentRepository.deleteById(id);
    }
}