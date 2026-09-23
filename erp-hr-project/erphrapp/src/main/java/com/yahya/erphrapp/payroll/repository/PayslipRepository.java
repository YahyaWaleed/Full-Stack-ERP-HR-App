package com.yahya.erphrapp.payroll.repository;

import com.yahya.erphrapp.payroll.entity.Payslip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PayslipRepository extends JpaRepository<Payslip, Long> {

    @EntityGraph(attributePaths = {"employee", "period"})
    Page<Payslip> findAllByPeriodId(Long periodId, Pageable pageable);

    @EntityGraph(attributePaths = {"employee", "period"})
    List<Payslip> findAllByEmployeeIdOrderByPeriodStartDateDesc(Long empId);

    @EntityGraph(attributePaths = {"employee", "period"})
    Optional<Payslip> findById(Long id);
}
