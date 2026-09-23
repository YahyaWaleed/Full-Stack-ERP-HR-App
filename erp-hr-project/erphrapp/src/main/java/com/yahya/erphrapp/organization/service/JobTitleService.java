package com.yahya.erphrapp.organization.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import com.yahya.erphrapp.exception.ResourceNotFoundException;
import com.yahya.erphrapp.organization.dto.JobTitleResponse;
import com.yahya.erphrapp.organization.entity.JobTitle;
import com.yahya.erphrapp.organization.mapper.JobTitleMapper;
import com.yahya.erphrapp.organization.repository.JobTitleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobTitleService {

    // inject repo and mapper
    private final JobTitleRepository jobTitleRepository;
    private final JobTitleMapper jobTitleMapper;
    public JobTitleService(JobTitleRepository jobTitleRepository, JobTitleMapper jobTitleMapper) {
        this.jobTitleRepository = jobTitleRepository;
        this.jobTitleMapper = jobTitleMapper;
    }

    // get all job titles
    @Cacheable("jobTitles")
    @Transactional(readOnly = true)
    public List<JobTitleResponse> getJobTitles() {
        List<JobTitleResponse> titles = jobTitleRepository.findAll().stream().map(jobTitleMapper::toResponse).toList();
        return titles;
    }

    // get one job title
    @Transactional(readOnly = true)
    public JobTitleResponse getJobTitle(Long id) {
        JobTitle title = jobTitleRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Job Title" , id));
        return jobTitleMapper.toResponse(title);
    }
}
