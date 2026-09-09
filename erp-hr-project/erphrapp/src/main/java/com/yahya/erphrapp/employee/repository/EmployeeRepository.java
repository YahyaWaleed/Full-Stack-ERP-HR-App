package com.yahya.erphrapp.employee.repository;

import com.yahya.erphrapp.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {
    Optional<Employee> findTopByOrderByEmpCodeDesc(); // will use in creating the empCode automatically

    List<Employee> findAllByBranchId(Long branchId);

    List<Employee> findAllByDepartmentId(Long deptId);

    List<Employee> findAllByJobTitleId(Long jobTitleId);
}
