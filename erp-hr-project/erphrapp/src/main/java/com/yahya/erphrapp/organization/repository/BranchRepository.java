package com.yahya.erphrapp.organization.repository;

import com.yahya.erphrapp.organization.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch,Long> {
    // this does all basic CRUD for me

}
