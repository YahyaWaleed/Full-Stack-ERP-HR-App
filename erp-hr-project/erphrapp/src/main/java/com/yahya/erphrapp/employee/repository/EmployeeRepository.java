package com.yahya.erphrapp.employee.repository;

import com.yahya.erphrapp.employee.entity.Employee;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {
    Optional<Employee> findTopByOrderByEmpCodeDesc();

    @EntityGraph(attributePaths = {"department", "jobTitle", "branch", "manager"})
    List<Employee> findAllByBranchId(Long branchId);

    @EntityGraph(attributePaths = {"department", "jobTitle", "branch", "manager"})
    List<Employee> findAllByDepartmentId(Long deptId);

    @EntityGraph(attributePaths = {"department", "jobTitle", "branch", "manager"})
    List<Employee> findAllByJobTitleId(Long jobTitleId);

    @EntityGraph(attributePaths = {"department", "jobTitle", "branch", "manager"})
    Page<Employee> findAllBy(Pageable pageable);

    @EntityGraph(attributePaths = {"department", "jobTitle", "branch", "manager"})
    Optional<Employee> findById(Long id);

    long countByEmpStatus(Employee.EmployeeStatus status);
}