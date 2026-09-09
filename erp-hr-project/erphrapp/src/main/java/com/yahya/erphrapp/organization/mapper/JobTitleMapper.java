package com.yahya.erphrapp.organization.mapper;

import com.yahya.erphrapp.organization.dto.JobTitleRequest;
import com.yahya.erphrapp.organization.dto.JobTitleResponse;
import com.yahya.erphrapp.organization.entity.JobTitle;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobTitleMapper {

    JobTitleResponse toResponse(JobTitle jobTitle);
    JobTitle toEntity(JobTitleRequest request);
}
