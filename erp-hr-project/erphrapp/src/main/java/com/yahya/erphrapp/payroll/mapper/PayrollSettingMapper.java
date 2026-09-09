package com.yahya.erphrapp.payroll.mapper;

import com.yahya.erphrapp.payroll.dto.PayrollSettingResponse;
import com.yahya.erphrapp.payroll.entity.PayrollSetting;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PayrollSettingMapper {

    PayrollSettingResponse toResponse(PayrollSetting payrollSetting);
}