package com.yahya.erphrapp.payroll.repository;

import com.yahya.erphrapp.payroll.entity.TaxBracket;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaxBracketRepository extends JpaRepository<TaxBracket, Long> {

    @EntityGraph(attributePaths = {"payrollSetting"})
    List<TaxBracket> findAllByOrderByPayrollSettingFiscalYearDescFromAmountAsc();

    @EntityGraph(attributePaths = {"payrollSetting"})
    List<TaxBracket> findByPayrollSettingFiscalYearOrderByFromAmountAsc(int fiscalYear);
}
