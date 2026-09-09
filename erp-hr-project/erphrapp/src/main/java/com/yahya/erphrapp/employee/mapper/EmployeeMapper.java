package com.yahya.erphrapp.employee.mapper;

import com.yahya.erphrapp.employee.dto.EmployeeRequest;
import com.yahya.erphrapp.employee.dto.EmployeeResponse;
import com.yahya.erphrapp.employee.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(source = "department.nameEn", target = "departmentName")
    @Mapping(source = "jobTitle.titleEn", target = "jobTitleName")
    @Mapping(source = "branch.nameEn", target = "branchName")
    @Mapping(source = "manager.fullNameEn", target = "managerName")
    EmployeeResponse toResponse(Employee employee);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "jobTitle", ignore = true)
    @Mapping(target = "branch", ignore = true)
    @Mapping(target = "manager", ignore = true)
    Employee toEntity(EmployeeRequest request);
}