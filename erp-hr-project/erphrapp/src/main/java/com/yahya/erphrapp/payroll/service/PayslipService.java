package com.yahya.erphrapp.payroll.service;

import com.yahya.erphrapp.exception.ResourceNotFoundException;
import com.yahya.erphrapp.payroll.dto.PayslipLineResponse;
import com.yahya.erphrapp.payroll.dto.PayslipResponse;
import com.yahya.erphrapp.payroll.entity.PayrollPeriod;
import com.yahya.erphrapp.payroll.mapper.PayslipLineMapper;
import com.yahya.erphrapp.payroll.mapper.PayslipMapper;
import com.yahya.erphrapp.payroll.repository.PayrollPeriodRepository;
import com.yahya.erphrapp.payroll.repository.PayslipLineRepository;
import com.yahya.erphrapp.payroll.repository.PayslipRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PayslipService {

    private final PayslipRepository payslipRepository;
    private final PayslipLineRepository payslipLineRepository;
    private final PayslipMapper payslipMapper;
    private final PayslipLineMapper payslipLineMapper;
    private final PayrollPeriodRepository payrollPeriodRepository;

    public PayslipService(PayrollPeriodRepository payrollPeriodRepository, PayslipRepository payslipRepository,
                          PayslipLineRepository payslipLineRepository, PayslipMapper payslipMapper,
                          PayslipLineMapper payslipLineMapper) {
        this.payslipLineRepository = payslipLineRepository;
        this.payslipMapper = payslipMapper;
        this.payslipRepository = payslipRepository;
        this.payslipLineMapper = payslipLineMapper;
        this.payrollPeriodRepository = payrollPeriodRepository;
    }

    // read one payslip by its id
    @Transactional(readOnly = true)
    public PayslipResponse getPayslip(Long id) {
        return payslipMapper.toResponse(payslipRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payslip", id)));
    }

    // the payslips of one period
    @Transactional(readOnly = true)
    public Page<PayslipResponse> getPayslipsByPeriodCode(String periodCode, Pageable pageable) {
        PayrollPeriod period = payrollPeriodRepository.findByPeriodCode(periodCode)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll Period", periodCode));
        return payslipRepository.findAllByPeriodId(period.getId(), pageable).map(payslipMapper::toResponse);
    }

    // the lines of one payslip, in print order
    @Transactional(readOnly = true)
    public List<PayslipLineResponse> getPayslipLines(Long payslipId) {
        if (!payslipRepository.existsById(payslipId)) {
            throw new ResourceNotFoundException("Payslip", payslipId);
        }
        return payslipLineRepository.findAllByPayslipIdOrderByPrintOrder(payslipId).stream()
                .map(payslipLineMapper::toResponse).toList();
    }

    // one employee's payslips, newest period first
    @Transactional(readOnly = true)
    public List<PayslipResponse> getPayslipsByEmployeeId(Long empId) {
        return payslipRepository.findAllByEmployeeIdOrderByPeriodStartDateDesc(empId).stream()
                .map(payslipMapper::toResponse).toList();
    }
}
