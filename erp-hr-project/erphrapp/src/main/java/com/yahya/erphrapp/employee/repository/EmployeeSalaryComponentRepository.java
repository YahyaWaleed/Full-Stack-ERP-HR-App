package com.yahya.erphrapp.employee.repository;

import com.yahya.erphrapp.employee.entity.EmployeeSalaryComponent;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeSalaryComponentRepository extends JpaRepository<EmployeeSalaryComponent, Long> {

    @EntityGraph(attributePaths = {"employee", "salaryComponent"})
    List<EmployeeSalaryComponent> findByEmployeeIdOrderByEffectiveFromDesc(Long empId);

    @EntityGraph(attributePaths = {"employee", "salaryComponent"})
    Optional<EmployeeSalaryComponent> findByIdAndEmployeeId(Long id, Long empId);
}
