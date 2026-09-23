package com.yahya.erphrapp.employee.repository;

import com.yahya.erphrapp.employee.entity.Employee;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee,Long>, JpaSpecificationExecutor<Employee> {

    // every employee list (with or without filters) loads its associations in the same query -- no N+1
    @Override
    @EntityGraph(attributePaths = {"department", "jobTitle", "branch", "manager"})
    Page<Employee> findAll(Specification<Employee> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"department", "jobTitle", "branch", "manager"})
    Optional<Employee> findById(Long id);

    // SELECT ... FOR UPDATE: serialises changes that must not interleave for one employee (contract numbers, termination)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Employee e where e.id = :id")
    Optional<Employee> findByIdForUpdate(Long id);

    long countByEmpStatus(Employee.EmployeeStatus status);
}
