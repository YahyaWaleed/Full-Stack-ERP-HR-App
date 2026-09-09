package com.yahya.erphrapp.payroll.service;

import com.yahya.erphrapp.exception.ResourceNotFoundException;
import com.yahya.erphrapp.payroll.dto.PayrollSettingResponse;
import com.yahya.erphrapp.payroll.entity.PayrollSetting;
import com.yahya.erphrapp.payroll.mapper.PayrollSettingMapper;
import com.yahya.erphrapp.payroll.repository.PayrollSettingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayrollSettingService {

    private final PayrollSettingRepository payrollSettingRepository;
    private final PayrollSettingMapper payrollSettingMapper;

    public PayrollSettingService(PayrollSettingRepository payrollSettingRepository,
                                 PayrollSettingMapper payrollSettingMapper) {
        this.payrollSettingRepository = payrollSettingRepository;
        this.payrollSettingMapper = payrollSettingMapper;
    }

    // read all payroll settings (one per fiscal year)
    public List<PayrollSettingResponse> getSettings() {
        return payrollSettingRepository.findAll()
                .stream()
                .map(payrollSettingMapper::toResponse)
                .toList();
    }

    // read the settings for one specific fiscal year
    public PayrollSettingResponse getSetting(int fiscalYear) {
        PayrollSetting setting = payrollSettingRepository.findById(fiscalYear)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll Setting", fiscalYear));
        return payrollSettingMapper.toResponse(setting);
    }
}