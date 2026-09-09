package com.yahya.erphrapp.payroll.mapper;

import com.yahya.erphrapp.payroll.dto.PayslipResponse;
import com.yahya.erphrapp.payroll.entity.Payslip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = PayslipLineMapper.class)
public interface PayslipMapper {

    @Mapping(source = "period.periodCode", target = "periodCode")
    @Mapping(source = "employee.empCode", target = "empCode")
    @Mapping(source = "employee.fullNameEn", target = "employeeName")
    @Mapping(target = "lines", ignore = true)
    PayslipResponse toResponse(Payslip payslip);
}