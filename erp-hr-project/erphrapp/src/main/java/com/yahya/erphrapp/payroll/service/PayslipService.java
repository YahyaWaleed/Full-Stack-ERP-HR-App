package com.yahya.erphrapp.payroll.service;

import com.yahya.erphrapp.exception.ConflictException;
import com.yahya.erphrapp.exception.ResourceNotFoundException;
import com.yahya.erphrapp.payroll.dto.PayslipLineResponse;
import com.yahya.erphrapp.payroll.dto.PayslipResponse;
import com.yahya.erphrapp.payroll.entity.PayrollPeriod;
import com.yahya.erphrapp.payroll.entity.Payslip;
import com.yahya.erphrapp.payroll.entity.PayslipLine;
import com.yahya.erphrapp.payroll.mapper.PayslipLineMapper;
import com.yahya.erphrapp.payroll.mapper.PayslipMapper;
import com.yahya.erphrapp.payroll.repository.PayrollPeriodRepository;
import com.yahya.erphrapp.payroll.repository.PayslipLineRepository;
import com.yahya.erphrapp.payroll.repository.PayslipRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayslipService {

    // inject all needed stuff
    private final PayslipRepository payslipRepository;
    private final PayslipLineRepository payslipLineRepository;
    private final PayslipMapper payslipMapper;
    private final PayslipLineMapper payslipLineMapper;
    private final PayrollPeriodRepository payrollPeriodRepository;

    public PayslipService(PayrollPeriodRepository payrollPeriodRepository, PayslipRepository payslipRepository, PayslipLineRepository payslipLineRepository, PayslipMapper payslipMapper, PayslipLineMapper payslipLineMapper) {
        this.payslipLineRepository = payslipLineRepository;
        this.payslipMapper = payslipMapper;
        this.payslipRepository = payslipRepository;
        this.payslipLineMapper = payslipLineMapper;
        this.payrollPeriodRepository = payrollPeriodRepository;
    }

    // read one payslip by its id
    public PayslipResponse getPayslip(Long id) {
        Payslip payslip = payslipRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payslip", id));
        return payslipMapper.toResponse(payslip);
    }

    // read all payslips for a specific month by periodCode
    public List<PayslipResponse> getPayslipByPeriodCode(String periodCode) {

        // find period id
        PayrollPeriod period = payrollPeriodRepository.findByPeriodCode(periodCode).orElseThrow(() -> new ResourceNotFoundException("Payroll Period", periodCode));
        long periodId = period.getId();

        // get all payslips that have same period id
        List<Payslip> payslips = payslipRepository.findAllByPeriodId(periodId);
        return payslips.stream().map(payslipMapper::toResponse).toList();
    }

    // read one payslip as lines
    public List<PayslipLineResponse> getPayslipLines(Long payslipId) {
        // verify the payslip exists
        if (!payslipRepository.existsById(payslipId)) {
            throw new ResourceNotFoundException("Payslip", payslipId);
        }

        // find all payslip lines that have the same payslip id
        List<PayslipLine> payslipLines = payslipLineRepository.findAllByPayslipId(payslipId);

        // change to response and return
        return payslipLines.stream().map(payslipLineMapper::toResponse).toList();
    }

    // get all payslips for one employee by employeeID
    public List<PayslipResponse> getPayslipsByEmployeeId(Long empId) {
        List<Payslip> payslips = payslipRepository.findAllByEmployeeId(empId);
        return payslips.stream().map(payslipMapper::toResponse).toList();
    }
}
