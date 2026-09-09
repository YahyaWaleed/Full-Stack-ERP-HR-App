package com.yahya.erphrapp.organization.controller;

import com.yahya.erphrapp.organization.dto.JobTitleResponse;
import com.yahya.erphrapp.organization.entity.JobTitle;
import com.yahya.erphrapp.organization.service.JobTitleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobTitleController {
    // inject the service
    private  final JobTitleService jobTitleService;
    public JobTitleController(JobTitleService jobTitleService) {
        this.jobTitleService = jobTitleService;
    }

    @GetMapping
    public List<JobTitleResponse> getJobTitles() {
        return jobTitleService.getJobTitles();
    }

    @GetMapping("/{id}")
    public JobTitleResponse getJobTitle(@PathVariable Long id) {
        return jobTitleService.getJobTitle(id);
    }

}
