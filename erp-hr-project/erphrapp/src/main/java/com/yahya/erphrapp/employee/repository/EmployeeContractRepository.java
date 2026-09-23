package com.yahya.erphrapp.employee.repository;

import com.yahya.erphrapp.employee.entity.EmployeeContract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployeeContractRepository extends JpaRepository<EmployeeContract,Long> {

    @EntityGraph(attributePaths = {"employee"})
    Page<EmployeeContract> findAllBy(Pageable pageable);

    @EntityGraph(attributePaths = {"employee"})
    List<EmployeeContract> findByEmployeeIdOrderByStartDateDesc(Long empId);

    @EntityGraph(attributePaths = {"employee"})
    Optional<EmployeeContract> findById(Long id);

    Optional<EmployeeContract> findByEmployeeIdAndStatus(Long empId, EmployeeContract.ContractStatus contractStatus);

    @Query("select c.contractNo from EmployeeContract c where c.employee.id = :empId")
    List<String> findContractNosByEmployeeId(Long empId);
}
