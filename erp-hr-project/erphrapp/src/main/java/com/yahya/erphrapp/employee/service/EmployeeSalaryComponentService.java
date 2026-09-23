package com.yahya.erphrapp.employee.service;

import com.yahya.erphrapp.audit.AuditService;
import com.yahya.erphrapp.employee.dto.EmployeeSalaryComponentRequest;
import com.yahya.erphrapp.employee.dto.EmployeeSalaryComponentResponse;
import com.yahya.erphrapp.employee.entity.Employee;
import com.yahya.erphrapp.employee.entity.EmployeeSalaryComponent;
import com.yahya.erphrapp.employee.mapper.EmployeeSalaryComponentMapper;
import com.yahya.erphrapp.employee.repository.EmployeeRepository;
import com.yahya.erphrapp.employee.repository.EmployeeSalaryComponentRepository;
import com.yahya.erphrapp.exception.BadRequestException;
import com.yahya.erphrapp.exception.ResourceNotFoundException;
import com.yahya.erphrapp.payroll.entity.SalaryComponent;
import com.yahya.erphrapp.payroll.repository.SalaryComponentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeeSalaryComponentService {

    private final EmployeeSalaryComponentRepository employeeSalaryComponentRepository;
    private final EmployeeSalaryComponentMapper employeeSalaryComponentMapper;
    private final EmployeeRepository employeeRepository;
    private final SalaryComponentRepository salaryComponentRepository;
    private final AuditService auditService;

    public EmployeeSalaryComponentService(EmployeeSalaryComponentRepository employeeSalaryComponentRepository,
                                          EmployeeSalaryComponentMapper employeeSalaryComponentMapper,
                                          EmployeeRepository employeeRepository,
                                          SalaryComponentRepository salaryComponentRepository,
                                          AuditService auditService) {
        this.employeeSalaryComponentRepository = employeeSalaryComponentRepository;
        this.employeeSalaryComponentMapper = employeeSalaryComponentMapper;
        this.employeeRepository = employeeRepository;
        this.salaryComponentRepository = salaryComponentRepository;
        this.auditService = auditService;
    }

    // all salary components for one employee (current and ended), newest first
    @Transactional(readOnly = true)
    public List<EmployeeSalaryComponentResponse> getComponentsForEmployee(Long empId) {
        return employeeSalaryComponentRepository.findByEmployeeIdOrderByEffectiveFromDesc(empId)
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
        validateDates(request);

        EmployeeSalaryComponent component = employeeSalaryComponentMapper.toEntity(request);
        component.setEmployee(employee);
        component.setSalaryComponent(salaryComponent);
        employeeSalaryComponentRepository.save(component);

        auditService.record("SALARY_COMPONENT_ADDED", "EMPLOYEE", employee.getEmpCode(), describe(component));
        return employeeSalaryComponentMapper.toResponse(component);
    }

    // update a salary component
    @Transactional
    public EmployeeSalaryComponentResponse updateComponent(Long empId, Long id, EmployeeSalaryComponentRequest request) {
        EmployeeSalaryComponent component = find(empId, id);
        SalaryComponent salaryComponent = salaryComponentRepository.findById(request.getCompId())
                .orElseThrow(() -> new ResourceNotFoundException("Salary Component", request.getCompId()));
        validateDates(request);
        String before = describe(component);

        component.setSalaryComponent(salaryComponent);
        component.setAmount(request.getAmount());
        component.setPercentage(request.getPercentage());
        component.setEffectiveFrom(request.getEffectiveFrom());
        component.setEffectiveTo(request.getEffectiveTo());
        component.setNotes(request.getNotes());
        employeeSalaryComponentRepository.save(component);

        auditService.record("SALARY_COMPONENT_CHANGED", "EMPLOYEE", component.getEmployee().getEmpCode(),
                "before[" + before + "] after[" + describe(component) + "]");
        return employeeSalaryComponentMapper.toResponse(component);
    }

    // removing a component is effective-dated: once it has been in effect it's ended yesterday instead of
    // deleted, so earlier payslips keep their history. Only a component that never took effect is deleted.
    @Transactional
    public void deleteComponent(Long empId, Long id) {
        EmployeeSalaryComponent component = find(empId, id);
        LocalDate today = LocalDate.now();
        String code = component.getEmployee().getEmpCode();

        if (component.getEffectiveFrom().isBefore(today)) {
            LocalDate lastDay = today.minusDays(1);
            if (component.getEffectiveTo() == null || component.getEffectiveTo().isAfter(lastDay)) {
                component.setEffectiveTo(lastDay);
                employeeSalaryComponentRepository.save(component);
            }
            auditService.record("SALARY_COMPONENT_ENDED", "EMPLOYEE", code, describe(component));
        } else {
            employeeSalaryComponentRepository.delete(component);
            auditService.record("SALARY_COMPONENT_DELETED", "EMPLOYEE", code, describe(component));
        }
    }

    private EmployeeSalaryComponent find(Long empId, Long id) {
        return employeeSalaryComponentRepository.findByIdAndEmployeeId(id, empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee Salary Component", id));
    }

    private static void validateDates(EmployeeSalaryComponentRequest request) {
        if (request.getEffectiveTo() != null && request.getEffectiveTo().isBefore(request.getEffectiveFrom())) {
            throw new BadRequestException("effectiveTo must be on or after effectiveFrom");
        }
    }

    private static String describe(EmployeeSalaryComponent c) {
        return c.getSalaryComponent().getCode() + " amount=" + c.getAmount() + " pct=" + c.getPercentage()
                + " " + c.getEffectiveFrom() + ".." + (c.getEffectiveTo() == null ? "" : c.getEffectiveTo());
    }
}
