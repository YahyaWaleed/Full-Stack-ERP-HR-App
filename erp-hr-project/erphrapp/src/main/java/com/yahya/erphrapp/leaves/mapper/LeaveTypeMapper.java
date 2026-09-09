package com.yahya.erphrapp.leaves.mapper;

import com.yahya.erphrapp.leaves.dto.LeaveTypeResponse;
import com.yahya.erphrapp.leaves.entity.LeaveType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LeaveTypeMapper {

    LeaveTypeResponse toResponse(LeaveType leaveType);
}