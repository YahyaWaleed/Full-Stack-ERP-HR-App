package com.yahya.erphrapp.employee.mapper;

import com.yahya.erphrapp.employee.dto.EmployeeRequest;
import com.yahya.erphrapp.employee.dto.EmployeeResponse;
import com.yahya.erphrapp.employee.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(source = "department.nameEn", target = "departmentName")
    @Mapping(source = "jobTitle.titleEn", target = "jobTitleName")
    @Mapping(source = "branch.nameEn", target = "branchName")
    @Mapping(source = "manager.fullNameEn", target = "managerName")
    @Mapping(source = "department.id", target = "deptId")
    @Mapping(source = "jobTitle.id", target = "jobId")
    @Mapping(source = "branch.id", target = "branchId")
    @Mapping(source = "manager.id", target = "managerId")
    EmployeeResponse toResponse(Employee employee);

    // the single place request fields are copied onto an employee -- used by both create and update.
    // Associations are resolved by id in the service; status, code and audit columns are never client-set.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "empCode", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "jobTitle", ignore = true)
    @Mapping(target = "branch", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "empStatus", ignore = true)
    @Mapping(target = "terminationDate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "maritalStatus", defaultValue = "SINGLE")
    @Mapping(target = "paymentMethod", defaultValue = "BANK")
    void copyFields(EmployeeRequest request, @MappingTarget Employee employee);
}
