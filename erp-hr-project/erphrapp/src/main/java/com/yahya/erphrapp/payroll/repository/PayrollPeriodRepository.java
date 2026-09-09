package com.yahya.erphrapp.payroll.repository;

import com.yahya.erphrapp.payroll.entity.PayrollPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PayrollPeriodRepository extends JpaRepository<PayrollPeriod, Long> {

    Optional<PayrollPeriod> findByPeriodCode(String periodCode);
    List<PayrollPeriod> findByFiscalYear(int fiscalYear);
}
