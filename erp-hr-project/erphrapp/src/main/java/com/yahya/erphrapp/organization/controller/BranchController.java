package com.yahya.erphrapp.organization.controller;

import com.yahya.erphrapp.organization.dto.BranchResponse;
import com.yahya.erphrapp.organization.service.BranchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
public class BranchController {

    // inject the service object
    private final BranchService branchService;
    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    //get all branches
    @GetMapping()
    public List<BranchResponse> getBranches() {
        return branchService.getBranches();
    }

    //get one branch
    @GetMapping("/{id}")
    public BranchResponse getBranch(@PathVariable Long id) {
        return branchService.getBranch(id);
    }
}
