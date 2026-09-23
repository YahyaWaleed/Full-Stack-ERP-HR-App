package com.yahya.erphrapp.payroll.repository;

import com.yahya.erphrapp.payroll.entity.PayrollPeriod;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PayrollPeriodRepository extends JpaRepository<PayrollPeriod, Long> {

    @EntityGraph(attributePaths = {"approvedBy"})
    Optional<PayrollPeriod> findByPeriodCode(String periodCode);

    @EntityGraph(attributePaths = {"approvedBy"})
    List<PayrollPeriod> findAllByOrderByPeriodCodeDesc();

    @EntityGraph(attributePaths = {"approvedBy"})
    List<PayrollPeriod> findByFiscalYearOrderByPeriodCodeDesc(int fiscalYear);
}
