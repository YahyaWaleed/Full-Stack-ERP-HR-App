package com.yahya.erphrapp.leaves.mapper;

import com.yahya.erphrapp.leaves.dto.LeaveBalanceResponse;
import com.yahya.erphrapp.leaves.entity.LeaveBalance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeaveBalanceMapper {

    @Mapping(source = "employee.empCode", target = "empCode")
    @Mapping(source = "employee.fullNameEn", target = "employeeName")
    @Mapping(source = "leaveType.nameEn", target = "leaveTypeName")
    LeaveBalanceResponse toResponse(LeaveBalance leaveBalance);
}