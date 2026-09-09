package com.yahya.erphrapp.payroll.service;

import com.yahya.erphrapp.exception.ResourceNotFoundException;
import com.yahya.erphrapp.payroll.dto.PayrollPaymentsResponse;
import com.yahya.erphrapp.payroll.entity.PayrollPayments;
import com.yahya.erphrapp.payroll.mapper.PayrollPaymentsMapper;
import com.yahya.erphrapp.payroll.repository.PayrollPaymentsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayrollPaymentsService {

    // inject mapper and repos
    private final PayrollPaymentsMapper payrollPaymentsMapper;
    private final PayrollPaymentsRepository payrollPaymentsRepository;

    public PayrollPaymentsService(PayrollPaymentsMapper payrollPaymentsMapper, PayrollPaymentsRepository payrollPaymentsRepository) {
        this.payrollPaymentsMapper = payrollPaymentsMapper;
        this.payrollPaymentsRepository = payrollPaymentsRepository;
    }

    // read a payment by its payment id
    public PayrollPaymentsResponse getPayment(Long id) {
        PayrollPayments payment = payrollPaymentsRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payroll Payment",id));
        return payrollPaymentsMapper.toResponse(payment);
    }

    // read all payments made in a specific period
    public List<PayrollPaymentsResponse> getPaymentsForPeriod(String periodCode) {
        List<PayrollPayments> payments = payrollPaymentsRepository.findByPayslipPeriodPeriodCode(periodCode);
        return payments.stream().map(payrollPaymentsMapper::toResponse).toList();
    }

    // read payment for a specific payslip
    public PayrollPaymentsResponse getPaymentForPayslip(Long payslipId) {
        PayrollPayments payment = payrollPaymentsRepository.findByPayslipId(payslipId).orElseThrow(() -> new ResourceNotFoundException("Payroll Payment for payslip", payslipId));
        return payrollPaymentsMapper.toResponse(payment);

    }
}
