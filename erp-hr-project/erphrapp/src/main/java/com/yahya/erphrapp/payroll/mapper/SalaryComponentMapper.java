package com.yahya.erphrapp.payroll.mapper;

import com.yahya.erphrapp.payroll.dto.SalaryComponentResponse;
import com.yahya.erphrapp.payroll.entity.SalaryComponent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SalaryComponentMapper {

    SalaryComponentResponse toResponse(SalaryComponent salaryComponent);
}