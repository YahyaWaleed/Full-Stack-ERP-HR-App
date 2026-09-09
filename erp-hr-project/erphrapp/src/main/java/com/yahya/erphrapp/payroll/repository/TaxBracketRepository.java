package com.yahya.erphrapp.payroll.repository;

import com.yahya.erphrapp.payroll.entity.TaxBracket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaxBracketRepository extends JpaRepository<TaxBracket, Long> {

    List<TaxBracket> findByPayrollSettingFiscalYear(int fiscalYear);
}