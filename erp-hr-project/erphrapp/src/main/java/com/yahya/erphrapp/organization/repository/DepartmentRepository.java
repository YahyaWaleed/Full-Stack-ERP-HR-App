package com.yahya.erphrapp.organization.repository;

import com.yahya.erphrapp.organization.entity.Department;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department,Long> {
    @EntityGraph(attributePaths = {"branch", "parentDepartment"})
    List<Department> findAllBy();

    @EntityGraph(attributePaths = {"branch", "parentDepartment"})
    Optional<Department> findById(Long id);
}
