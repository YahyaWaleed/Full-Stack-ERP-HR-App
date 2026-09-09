package com.yahya.erphrapp.payroll.mapper;

import com.yahya.erphrapp.payroll.dto.TaxBracketResponse;
import com.yahya.erphrapp.payroll.entity.TaxBracket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaxBracketMapper {

    @Mapping(source = "payrollSetting.fiscalYear", target = "fiscalYear")
    TaxBracketResponse toResponse(TaxBracket taxBracket);
}