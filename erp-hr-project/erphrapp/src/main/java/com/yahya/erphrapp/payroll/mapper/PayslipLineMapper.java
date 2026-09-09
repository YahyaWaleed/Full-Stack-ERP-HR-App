package com.yahya.erphrapp.payroll.mapper;

import com.yahya.erphrapp.payroll.dto.PayslipLineResponse;
import com.yahya.erphrapp.payroll.entity.PayslipLine;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PayslipLineMapper {

    PayslipLineResponse toResponse(PayslipLine payslipLine);
}