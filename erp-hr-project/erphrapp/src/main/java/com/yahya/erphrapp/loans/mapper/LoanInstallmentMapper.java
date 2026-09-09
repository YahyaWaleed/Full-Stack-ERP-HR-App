package com.yahya.erphrapp.loans.mapper;

import com.yahya.erphrapp.loans.dto.LoanInstallmentRequest;
import com.yahya.erphrapp.loans.dto.LoanInstallmentResponse;
import com.yahya.erphrapp.loans.entity.LoanInstallment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanInstallmentMapper {

    @Mapping(source = "loan.id", target = "loanId")
    @Mapping(source = "payslip.id", target = "payslipId")
    LoanInstallmentResponse toResponse(LoanInstallment installment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "loan", ignore = true)
    @Mapping(target = "payslip", ignore = true)
    LoanInstallment toEntity(LoanInstallmentRequest request);
}