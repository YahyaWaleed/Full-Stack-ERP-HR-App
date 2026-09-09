package com.yahya.erphrapp.authentication.controller;

import com.yahya.erphrapp.authentication.dto.LoginRequest;
import com.yahya.erphrapp.authentication.dto.LoginResponse;
import com.yahya.erphrapp.authentication.entity.HrUser;
import com.yahya.erphrapp.authentication.repository.HrUserRepository;
import com.yahya.erphrapp.authentication.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final HrUserRepository hrUserRepository;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, HrUserRepository hrUserRepository, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.hrUserRepository = hrUserRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        // this line actually checks the username/password -
        // it throws an exception automatically if they're wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // if we reach this line, the login was correct
        HrUser hrUser = hrUserRepository.findByUsername(loginRequest.getUsername()).orElseThrow(); // won't actually happen, since authenticate() already confirmed the user exists

        String token = jwtUtil.generateToken(hrUser.getUsername(), hrUser.getRole().name());

        return new LoginResponse(token, hrUser.getRole().name());
    }
}