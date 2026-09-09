package com.yahya.erphrapp.payroll.service;

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
    // inject mapper and repo
    private final PayrollPeriodMapper payrollPeriodMapper;
    private final PayrollPeriodRepository payrollPeriodRepository;

    private final EntityManager entityManager; // to be able to write queries execute the functions in the db itself

    public  PayrollPeriodService(EntityManager entityManager, PayrollPeriodMapper payrollPeriodMapper, PayrollPeriodRepository payrollPeriodRepository) {
        this.payrollPeriodMapper = payrollPeriodMapper;
        this.payrollPeriodRepository = payrollPeriodRepository;
        this.entityManager = entityManager;
    }

    // read all periods
    public List<PayrollPeriodResponse> getPeriods() {
        return payrollPeriodRepository.findAll().stream().map(payrollPeriodMapper::toResponse).toList();
    }

    // read one period by its period code
    public PayrollPeriodResponse getPeriod(String periodCode) {
        PayrollPeriod period = payrollPeriodRepository.findByPeriodCode(periodCode).orElseThrow(() -> new ResourceNotFoundException("Payroll Period", periodCode));
        return payrollPeriodMapper.toResponse(period);
    }


    // read all periods in the same fiscal year
    public List<PayrollPeriodResponse> getPeriodsInFiscalYear(int fiscalYear) {
        return payrollPeriodRepository.findByFiscalYear(fiscalYear).stream().map(payrollPeriodMapper::toResponse).toList();
    }

    @Transactional
    public PayrollPeriodResponse createPeriod(PayrollPeriodRequest payrollPeriodRequest) {

        // validate end date is after start date
        if (payrollPeriodRequest.getStartDate().isAfter(payrollPeriodRequest.getEndDate())) {
            throw new ConflictException("Start date must be before or equal to end date");
        }

        // validate that pay date is within the payroll period
        if (payrollPeriodRequest.getPayDate().isBefore(payrollPeriodRequest.getStartDate())
                || payrollPeriodRequest.getPayDate().isAfter(payrollPeriodRequest.getEndDate())) {
            throw new ConflictException("Pay date must be within the payroll period");
        }

        PayrollPeriod period = new PayrollPeriod();

        period.setPeriodCode(payrollPeriodRequest.getPeriodCode());
        period.setFiscalYear(payrollPeriodRequest.getFiscalYear());
        period.setStartDate(payrollPeriodRequest.getStartDate());
        period.setEndDate(payrollPeriodRequest.getEndDate());
        period.setPayDate(payrollPeriodRequest.getPayDate());
        period.setWorkingDays(payrollPeriodRequest.getWorkingDays());
        period.setStatus(PayrollPeriod.PeriodStatus.OPEN);

        payrollPeriodRepository.save(period);

        return payrollPeriodMapper.toResponse(period);
    }

    // generate payslips for all employees in a specific period
    @Transactional
    public void runPayroll(String periodCode) {

        // find the period by its period code
        PayrollPeriod period = payrollPeriodRepository.findByPeriodCode(periodCode).orElseThrow(() -> new ResourceNotFoundException("Payroll Period", periodCode));

        // check that the period is OPEN
        if (period.getStatus() != PayrollPeriod.PeriodStatus.OPEN) {
            throw new ConflictException("Period Status must be OPEN to run payroll");
        }

        // call the sp_run_payroll function
        entityManager.createNativeQuery("CALL sp_run_payroll(:code)")
                .setParameter("code",periodCode)
                .executeUpdate();

        // update the status and processedAt
        period.setStatus(PayrollPeriod.PeriodStatus.PROCESSED);
        period.setProcessedAt(LocalDateTime.now());

        // save the period
        payrollPeriodRepository.save(period);
    }

    // mark the periods as paid
    @Transactional
    public void payPeriod(String periodCode, String method) {

        // find period by its period code
        PayrollPeriod period = payrollPeriodRepository.findByPeriodCode(periodCode).orElseThrow(() -> new ResourceNotFoundException("Payroll Period", periodCode));;

        // check that period status is processed
        if (period.getStatus() != PayrollPeriod.PeriodStatus.PROCESSED) {
            throw new ConflictException("Period Status must be PROCESSED to pay the period");
        }

        // call the sp_pay_period
        entityManager.createNativeQuery("CALL sp_pay_period(:code, :method)")
                .setParameter("code", periodCode)
                .setParameter("method", method)
                .executeUpdate();

        // update the status to paid
        period.setStatus(PayrollPeriod.PeriodStatus.PAID);

        // save period
        payrollPeriodRepository.save(period);
    }
}
