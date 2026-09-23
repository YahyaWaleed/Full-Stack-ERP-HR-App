package com.yahya.erphrapp.loans.repository;

import com.yahya.erphrapp.loans.entity.LoanInstallment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {

    @EntityGraph(attributePaths = {"loan", "payslip"})
    List<LoanInstallment> findAllByLoanIdOrderByPeriodCode(Long loanId);
}
