package com.yahya.erphrapp.loans.mapper;

import com.yahya.erphrapp.loans.dto.LoanInstallmentResponse;
import com.yahya.erphrapp.loans.entity.LoanInstallment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// installments are only ever written by sp_run_payroll, so there's no request -> entity mapping
@Mapper(componentModel = "spring")
public interface LoanInstallmentMapper {

    @Mapping(source = "loan.id", target = "loanId")
    @Mapping(source = "payslip.id", target = "payslipId")
    LoanInstallmentResponse toResponse(LoanInstallment installment);
}
