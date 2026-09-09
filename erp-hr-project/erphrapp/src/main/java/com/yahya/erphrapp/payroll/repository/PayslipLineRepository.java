package com.yahya.erphrapp.payroll.repository;

import com.yahya.erphrapp.payroll.entity.PayslipLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayslipLineRepository extends JpaRepository<PayslipLine, Long> {
    List<PayslipLine> findAllByPayslipId(Long payslipId);
}
