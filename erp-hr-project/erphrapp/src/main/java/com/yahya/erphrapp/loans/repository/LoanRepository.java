package com.yahya.erphrapp.loans.repository;

import com.yahya.erphrapp.loans.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan,Long> {

    @EntityGraph(attributePaths = {"employee", "approvedBy"})
    Page<Loan> findAllBy(Pageable pageable);

    @EntityGraph(attributePaths = {"employee", "approvedBy"})
    Page<Loan> findAllByEmployeeId(Long empId, Pageable pageable);

    @EntityGraph(attributePaths = {"employee", "approvedBy"})
    Optional<Loan> findById(Long id);

    List<Loan> findAllByEmployeeId(Long employeeId);

    long countByStatus(Loan.LoanStatus status);
}
