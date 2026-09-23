package com.yahya.erphrapp.payroll.mapper;

import com.yahya.erphrapp.payroll.dto.PayrollPeriodResponse;
import com.yahya.erphrapp.payroll.entity.PayrollPeriod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PayrollPeriodMapper {

    @Mapping(source = "approvedBy.fullNameEn", target = "approvedByName")
    PayrollPeriodResponse toResponse(PayrollPeriod period);
}
