package com.yahya.erphrapp.leaves.mapper;

import com.yahya.erphrapp.leaves.dto.LeaveRequestResponse;
import com.yahya.erphrapp.leaves.entity.LeaveRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeaveRequestMapper {

    @Mapping(source = "employee.empCode", target = "empCode")
    @Mapping(source = "employee.fullNameEn", target = "employeeName")
    @Mapping(source = "leaveType.nameEn", target = "leaveTypeName")
    @Mapping(source = "approver.fullNameEn", target = "approverName")
    LeaveRequestResponse toResponse(LeaveRequest leaveRequest);
}
