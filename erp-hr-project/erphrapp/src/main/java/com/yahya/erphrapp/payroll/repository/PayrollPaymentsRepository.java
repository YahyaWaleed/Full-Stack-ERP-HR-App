package com.yahya.erphrapp.payroll.repository;

import com.yahya.erphrapp.payroll.entity.PayrollPayments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PayrollPaymentsRepository extends JpaRepository<PayrollPayments,Long> {

    List<PayrollPayments> findByPayslipPeriodPeriodCode(String periodCode);
    Optional<PayrollPayments> findByPayslipId(Long payslipId);
}
