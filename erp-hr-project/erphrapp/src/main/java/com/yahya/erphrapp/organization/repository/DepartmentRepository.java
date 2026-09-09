package com.yahya.erphrapp.organization.repository;

import com.yahya.erphrapp.organization.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department,Long> {
}
