package com.yahya.erphrapp.authentication.repository;

import com.yahya.erphrapp.authentication.entity.HrUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HrUserRepository extends JpaRepository<HrUser, Long> {

    Optional<HrUser> findByUsername(String username);
}