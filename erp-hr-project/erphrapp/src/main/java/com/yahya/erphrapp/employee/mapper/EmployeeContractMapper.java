package com.yahya.erphrapp.employee.mapper;

import com.yahya.erphrapp.employee.dto.EmployeeContractRequest;
import com.yahya.erphrapp.employee.dto.EmployeeContractResponse;
import com.yahya.erphrapp.employee.entity.EmployeeContract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeContractMapper {

    @Mapping(source = "employee.empCode", target = "empCode")
    EmployeeContractResponse toResponse(EmployeeContract contract);

    // defaults match the column defaults in the schema
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "contractNo", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "currency", defaultValue = "EGP")
    @Mapping(target = "weeklyHours", defaultValue = "40.0")
    @Mapping(target = "annualLeaveDays", defaultValue = "21")
    @Mapping(target = "probationMonths", defaultValue = "3")
    EmployeeContract toEntity(EmployeeContractRequest request);
}
