package com.yahya.erphrapp.authentication.controller;

import com.yahya.erphrapp.audit.AuditService;
import com.yahya.erphrapp.authentication.entity.HrUser;
import com.yahya.erphrapp.authentication.repository.HrUserRepository;
import com.yahya.erphrapp.authentication.security.RefreshTokenService;
import com.yahya.erphrapp.exception.BadRequestException;
import com.yahya.erphrapp.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

// revoking access (review 9.5): a disabled user can't log in, and all their refresh tokens are revoked at once,
// so any session ends when its current access token expires (15 minutes by default)
@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('HR_ADMIN')")
public class UserAdminController {

    private final HrUserRepository hrUserRepository;
    private final RefreshTokenService refreshTokenService;
    private final AuditService auditService;

    public UserAdminController(HrUserRepository hrUserRepository, RefreshTokenService refreshTokenService, AuditService auditService) {
        this.hrUserRepository = hrUserRepository;
        this.refreshTokenService = refreshTokenService;
        this.auditService = auditService;
    }

    @PostMapping("/{username}/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void disable(@PathVariable String username, Authentication current) {
        if (username.equalsIgnoreCase(current.getName())) {
            throw new BadRequestException("You cannot disable your own account");
        }
        HrUser user = find(username);
        user.setEnabled(false);
        refreshTokenService.revokeAllFor(user);
        auditService.record("USER_DISABLED", "USER", username, null);
    }

    @PostMapping("/{username}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void enable(@PathVariable String username) {
        find(username).setEnabled(true);
        auditService.record("USER_ENABLED", "USER", username, null);
    }

    private HrUser find(String username) {
        return hrUserRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", username));
    }
}
