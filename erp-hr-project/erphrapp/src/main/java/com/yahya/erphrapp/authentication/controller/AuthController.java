package com.yahya.erphrapp.authentication.controller;

import com.yahya.erphrapp.authentication.dto.LoginRequest;
import com.yahya.erphrapp.authentication.dto.LoginResponse;
import com.yahya.erphrapp.authentication.entity.HrUser;
import com.yahya.erphrapp.authentication.repository.HrUserRepository;
import com.yahya.erphrapp.authentication.security.JwtUtil;
import com.yahya.erphrapp.authentication.security.LoginAttemptService;
import com.yahya.erphrapp.authentication.security.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

// Short-lived access token (JSON body, kept in memory by the client) + long-lived refresh token in an
// HttpOnly, SameSite=Strict cookie that JavaScript can't read and that is only sent to /api/v1/auth.
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    static final String REFRESH_COOKIE = "erp_refresh";
    private static final String COOKIE_PATH = "/api/v1/auth";

    private final AuthenticationManager authenticationManager;
    private final HrUserRepository hrUserRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final LoginAttemptService loginAttemptService;
    private final boolean secureCookie;

    public AuthController(AuthenticationManager authenticationManager, HrUserRepository hrUserRepository, JwtUtil jwtUtil,
                          RefreshTokenService refreshTokenService, LoginAttemptService loginAttemptService,
                          @Value("${app.auth.cookie-secure:true}") boolean secureCookie) {
        this.authenticationManager = authenticationManager;
        this.hrUserRepository = hrUserRepository;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.loginAttemptService = loginAttemptService;
        this.secureCookie = secureCookie;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        String username = loginRequest.getUsername();
        String ip = request.getRemoteAddr();
        loginAttemptService.checkAllowed(username, ip); // 429 while locked out

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, loginRequest.getPassword()));
        } catch (AuthenticationException e) {
            loginAttemptService.recordFailure(username, ip);
            throw e; // 401 "Invalid username or password"
        }
        loginAttemptService.recordSuccess(username);

        HrUser hrUser = hrUserRepository.findByUsername(username).orElseThrow(); // authenticate() already confirmed it exists
        refreshTokenService.purgeExpired();
        setRefreshCookie(response, refreshTokenService.issue(hrUser), refreshTokenService.getLifetime());
        return toResponse(hrUser);
    }

    // new access token from the refresh cookie; the cookie itself is rotated
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken,
                                                 HttpServletResponse response) {
        return refreshTokenService.rotate(refreshToken)
                .map(rotation -> {
                    setRefreshCookie(response, rotation.newToken(), refreshTokenService.getLifetime());
                    return ResponseEntity.ok(toResponse(rotation.user()));
                })
                .orElseGet(() -> {
                    setRefreshCookie(response, "", Duration.ZERO);
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                });
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken, HttpServletResponse response) {
        refreshTokenService.revoke(refreshToken);
        setRefreshCookie(response, "", Duration.ZERO);
    }

    private LoginResponse toResponse(HrUser user) {
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new LoginResponse(token, user.getRole().name(), user.getUsername(), jwtUtil.getExpirationSeconds());
    }

    private void setRefreshCookie(HttpServletResponse response, String value, Duration maxAge) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE, value)
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Strict")
                .path(COOKIE_PATH)
                .maxAge(maxAge)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
