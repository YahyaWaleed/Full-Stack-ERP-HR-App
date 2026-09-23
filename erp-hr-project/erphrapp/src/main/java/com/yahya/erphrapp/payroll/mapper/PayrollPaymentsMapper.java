package com.yahya.erphrapp.payroll.mapper;

import com.yahya.erphrapp.payroll.dto.PayrollPaymentsResponse;
import com.yahya.erphrapp.payroll.entity.PayrollPayments;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PayrollPaymentsMapper {

    @Mapping(source = "payslip.id", target = "payslipId")
    @Mapping(source = "payslip.employee.empCode", target = "empCode")
    @Mapping(source = "payslip.period.periodCode", target = "periodCode")
    PayrollPaymentsResponse toResponse(PayrollPayments payment);
}
