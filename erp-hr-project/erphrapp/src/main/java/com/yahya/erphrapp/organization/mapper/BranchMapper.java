package com.yahya.erphrapp.organization.mapper;

import com.yahya.erphrapp.organization.dto.BranchRequest;
import com.yahya.erphrapp.organization.dto.BranchResponse;
import com.yahya.erphrapp.organization.entity.Branch;
import org.mapstruct.Mapper;

// convert a branch entity to dto
@Mapper(componentModel = "spring")
public interface BranchMapper {

    BranchResponse toResponse(Branch branch);

    Branch toEntity(BranchRequest request);
}