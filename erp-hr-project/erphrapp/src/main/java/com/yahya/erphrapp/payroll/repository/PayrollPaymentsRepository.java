package com.yahya.erphrapp.payroll.repository;

import com.yahya.erphrapp.payroll.entity.PayrollPayments;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PayrollPaymentsRepository extends JpaRepository<PayrollPayments,Long> {

    @EntityGraph(attributePaths = {"payslip", "payslip.employee", "payslip.period"})
    List<PayrollPayments> findByPayslipPeriodPeriodCode(String periodCode);

    @EntityGraph(attributePaths = {"payslip", "payslip.employee", "payslip.period"})
    Optional<PayrollPayments> findByPayslipId(Long payslipId);

    @EntityGraph(attributePaths = {"payslip", "payslip.employee", "payslip.period"})
    Optional<PayrollPayments> findById(Long id);
}
