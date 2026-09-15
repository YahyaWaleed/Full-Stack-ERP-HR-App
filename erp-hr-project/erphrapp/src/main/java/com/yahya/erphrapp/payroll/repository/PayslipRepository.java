package com.yahya.erphrapp.payroll.repository;

import com.yahya.erphrapp.payroll.entity.Payslip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayslipRepository extends JpaRepository<Payslip, Long> {

    @EntityGraph(attributePaths = {"employee", "period"})
    Page<Payslip> findAllByPeriodId(Long periodId, Pageable pageable);

    List<Payslip> findAllByPeriodId(long periodId);
    List<Payslip> findAllByEmployeeId(long empId);
}
