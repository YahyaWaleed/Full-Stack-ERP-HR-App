package com.yahya.erphrapp.loans.mapper;

import com.yahya.erphrapp.loans.dto.LoanRequest;
import com.yahya.erphrapp.loans.dto.LoanResponse;
import com.yahya.erphrapp.loans.entity.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(source = "employee.id", target = "empId")
    @Mapping(source = "approvedBy.id", target = "approvedById")
    LoanResponse toResponse(Loan loan);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "approvedBy", ignore = true)
    Loan toEntity(LoanRequest request);
}