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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "status", ignore = true)
    EmployeeContract toEntity(EmployeeContractRequest request);
}