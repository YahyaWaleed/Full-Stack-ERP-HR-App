package com.yahya.erphrapp.organization.service;

import com.yahya.erphrapp.exception.ResourceNotFoundException;
import com.yahya.erphrapp.organization.dto.BranchResponse;
import com.yahya.erphrapp.organization.entity.Branch;
import com.yahya.erphrapp.organization.mapper.BranchMapper;
import com.yahya.erphrapp.organization.repository.BranchRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchService {

    // inject BranchMapper
    private final BranchMapper branchMapper;
    // inject the branch repository
    private final BranchRepository branchRepository;

    public BranchService(BranchRepository branchRepository, BranchMapper branchMapper) {
        this.branchRepository = branchRepository;
        this.branchMapper = branchMapper;
    }

    // get all branches
    public List<BranchResponse> getBranches() {
        return branchRepository.findAll().stream().map(branchMapper::toResponse).toList();
    }

    // get one branch
    public BranchResponse getBranch(Long branchId) {
        Branch branch = branchRepository.findById(branchId).orElseThrow(() -> new ResourceNotFoundException("Branch", branchId));
        return branchMapper.toResponse(branch);
    }
}
