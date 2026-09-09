package com.yahya.erphrapp.organization.mapper;

import com.yahya.erphrapp.organization.dto.BranchRequest;
import com.yahya.erphrapp.organization.dto.BranchResponse;
import com.yahya.erphrapp.organization.dto.DepartmentRequest;
import com.yahya.erphrapp.organization.dto.DepartmentResponse;
import com.yahya.erphrapp.organization.entity.Branch;
import com.yahya.erphrapp.organization.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    @Mapping(source = "branch.nameEn", target = "branchName")
    @Mapping(source = "parentDepartment.nameEn", target = "parentDeptName")
    @Mapping(source = "costCenter", target = "costCenter")
    DepartmentResponse toResponse(Department department);

    Department toEntity(DepartmentRequest request);
}
