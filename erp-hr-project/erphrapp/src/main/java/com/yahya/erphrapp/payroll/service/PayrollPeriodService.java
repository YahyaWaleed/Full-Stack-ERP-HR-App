package com.yahya.erphrapp.payroll.service;

import com.yahya.erphrapp.audit.AuditService;
import com.yahya.erphrapp.employee.entity.Employee;
import com.yahya.erphrapp.exception.BadRequestException;
import com.yahya.erphrapp.exception.ConflictException;
import com.yahya.erphrapp.exception.ResourceNotFoundException;
import com.yahya.erphrapp.payroll.dto.PayrollPeriodRequest;
import com.yahya.erphrapp.payroll.dto.PayrollPeriodResponse;
import com.yahya.erphrapp.payroll.entity.PayrollPeriod;
import com.yahya.erphrapp.payroll.mapper.PayrollPeriodMapper;
import com.yahya.erphrapp.payroll.repository.PayrollPeriodRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PayrollPeriodService {

    private final PayrollPeriodMapper payrollPeriodMapper;
    private final PayrollPeriodRepository payrollPeriodRepository;
    private final EntityManager entityManager; // runs the payroll procedures in the database
    private final AuditService auditService;

    public PayrollPeriodService(EntityManager entityManager, PayrollPeriodMapper payrollPeriodMapper,
                                PayrollPeriodRepository payrollPeriodRepository, AuditService auditService) {
        this.payrollPeriodMapper = payrollPeriodMapper;
        this.payrollPeriodRepository = payrollPeriodRepository;
        this.entityManager = entityManager;
        this.auditService = auditService;
    }

    // all periods, newest first; optionally one fiscal year (GET /payroll-periods?fiscalYear=2026)
    @Transactional(readOnly = true)
    public List<PayrollPeriodResponse> getPeriods(Integer fiscalYear) {
        List<PayrollPeriod> periods = fiscalYear == null
                ? payrollPeriodRepository.findAllByOrderByPeriodCodeDesc()
                : payrollPeriodRepository.findByFiscalYearOrderByPeriodCodeDesc(fiscalYear);
        return periods.stream().map(payrollPeriodMapper::toResponse).toList();
    }

    // read one period by its period code
    @Transactional(readOnly = true)
    public PayrollPeriodResponse getPeriod(String periodCode) {
        return payrollPeriodMapper.toResponse(find(periodCode));
    }

    @Transactional
    public PayrollPeriodResponse createPeriod(PayrollPeriodRequest request) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BadRequestException("Start date must be before or equal to end date");
        }
        if (request.getPayDate().isBefore(request.getStartDate()) || request.getPayDate().isAfter(request.getEndDate())) {
            throw new BadRequestException("Pay date must be within the payroll period");
        }
        if (!request.getPeriodCode().startsWith(String.valueOf(request.getFiscalYear()))) {
            throw new BadRequestException("Period code " + request.getPeriodCode() + " is not in fiscal year " + request.getFiscalYear());
        }
        if (payrollPeriodRepository.findByPeriodCode(request.getPeriodCode()).isPresent()) {
            throw new ConflictException("Payroll period " + request.getPeriodCode() + " already exists");
        }

        PayrollPeriod period = new PayrollPeriod();
        period.setPeriodCode(request.getPeriodCode());
        period.setFiscalYear(request.getFiscalYear());
        period.setStartDate(request.getStartDate());
        period.setEndDate(request.getEndDate());
        period.setPayDate(request.getPayDate());
        period.setWorkingDays(request.getWorkingDays() != null ? request.getWorkingDays() : 22);
        period.setStatus(PayrollPeriod.PeriodStatus.OPEN);
        payrollPeriodRepository.save(period);

        auditService.record("PERIOD_OPENED", "PAYROLL_PERIOD", period.getPeriodCode(),
                period.getStartDate() + ".." + period.getEndDate() + " pay " + period.getPayDate());
        return payrollPeriodMapper.toResponse(period);
    }

    // generate payslips for all employees in a period
    @Transactional
    public void runPayroll(String periodCode) {
        PayrollPeriod period = find(periodCode);
        if (period.getStatus() != PayrollPeriod.PeriodStatus.OPEN) {
            throw new ConflictException("Period Status must be OPEN to run payroll");
        }

        entityManager.createNativeQuery("CALL sp_run_payroll(:code)")
                .setParameter("code", periodCode)
                .executeUpdate();

        period.setStatus(PayrollPeriod.PeriodStatus.PROCESSED);
        period.setProcessedAt(LocalDateTime.now());
        payrollPeriodRepository.save(period);

        Object[] totals = (Object[]) entityManager.createNativeQuery("""
                        SELECT COUNT(*), IFNULL(SUM(p.net_pay), 0) FROM payslips p
                         WHERE p.period_id = :periodId""")
                .setParameter("periodId", period.getId())
                .getSingleResult();
        auditService.record("PAYROLL_RUN", "PAYROLL_PERIOD", periodCode, "payslips=" + totals[0] + " totalNet=" + totals[1]);
    }

    // mark the period as paid, creating one payment per payslip
    @Transactional
    public void payPeriod(String periodCode, Employee.PaymentMethod method) {
        PayrollPeriod period = find(periodCode);
        if (period.getStatus() != PayrollPeriod.PeriodStatus.PROCESSED) {
            throw new ConflictException("Period Status must be PROCESSED to pay the period");
        }

        entityManager.createNativeQuery("CALL sp_pay_period(:code, :method)")
                .setParameter("code", periodCode)
                .setParameter("method", method.name())
                .executeUpdate();

        period.setStatus(PayrollPeriod.PeriodStatus.PAID);
        payrollPeriodRepository.save(period);
        auditService.record("PAYROLL_PAID", "PAYROLL_PERIOD", periodCode, "method=" + method);
    }

    private PayrollPeriod find(String periodCode) {
        return payrollPeriodRepository.findByPeriodCode(periodCode)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll Period", periodCode));
    }
}
