package com.yahya.erphrapp.payroll.repository;

import com.yahya.erphrapp.payroll.entity.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayslipRepository extends JpaRepository<Payslip, Long> {

    List<Payslip> findAllByPeriodId(long periodId);
    List<Payslip> findAllByEmployeeId(long empId);
}
