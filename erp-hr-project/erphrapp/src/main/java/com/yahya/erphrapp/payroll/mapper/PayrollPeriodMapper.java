package com.yahya.erphrapp.payroll.mapper;

import com.yahya.erphrapp.payroll.dto.PayrollPeriodRequest;
import com.yahya.erphrapp.payroll.dto.PayrollPeriodResponse;
import com.yahya.erphrapp.payroll.entity.PayrollPeriod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PayrollPeriodMapper {

    @Mapping(source = "approvedBy.fullNameEn", target = "approvedByName")
    PayrollPeriodResponse toResponse(PayrollPeriod period);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "processedAt", ignore = true)
    @Mapping(target = "approvedBy", ignore = true)
    PayrollPeriod toEntity(PayrollPeriodRequest request);
}