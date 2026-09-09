package com.yahya.erphrapp.employee.repository;

import com.yahya.erphrapp.employee.entity.EmployeeContract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeContractRepository extends JpaRepository<EmployeeContract,Long> {

    List<EmployeeContract> findByEmployeeId(Long empId);
    Optional<EmployeeContract> findByEmployeeIdAndStatus(Long empId, EmployeeContract.ContractStatus contractStatus);

    Optional<EmployeeContract> findTopByOrderByContractNoDesc(); // will use in automatically generating the empCode
}
