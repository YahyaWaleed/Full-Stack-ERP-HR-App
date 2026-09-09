package com.yahya.erphrapp.employee.mapper;

import com.yahya.erphrapp.employee.dto.EmployeeSalaryComponentRequest;
import com.yahya.erphrapp.employee.dto.EmployeeSalaryComponentResponse;
import com.yahya.erphrapp.employee.entity.EmployeeSalaryComponent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeSalaryComponentMapper {

    @Mapping(source = "employee.empCode", target = "empCode")
    @Mapping(source = "salaryComponent.code", target = "componentCode")
    @Mapping(source = "salaryComponent.nameEn", target = "componentName")
    EmployeeSalaryComponentResponse toResponse(EmployeeSalaryComponent component);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "salaryComponent", ignore = true)
    EmployeeSalaryComponent toEntity(EmployeeSalaryComponentRequest request);
}