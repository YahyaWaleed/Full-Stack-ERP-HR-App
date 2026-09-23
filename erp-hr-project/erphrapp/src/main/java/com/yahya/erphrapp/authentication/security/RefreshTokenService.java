package com.yahya.erphrapp.authentication.security;

import com.yahya.erphrapp.authentication.entity.HrUser;
import com.yahya.erphrapp.authentication.entity.RefreshToken;
import com.yahya.erphrapp.authentication.repository.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

// Refresh tokens (review 9.5): random values handed to the browser in an HttpOnly cookie, stored only as a hash.
// Every refresh rotates the token. Presenting an already-rotated token means it was copied, so the whole
// session family of that user is revoked. Logging out or disabling a user revokes their tokens.
@Service
public class RefreshTokenService {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Duration REUSE_GRACE = Duration.ofSeconds(10);

    private final RefreshTokenRepository repository;
    private final Duration lifetime;

    public RefreshTokenService(RefreshTokenRepository repository,
                               @Value("${app.auth.refresh-token-days:7}") long lifetimeDays) {
        this.repository = repository;
        this.lifetime = Duration.ofDays(lifetimeDays);
    }

    public Duration getLifetime() {
        return lifetime;
    }

    // creates a new session for the user and returns the raw value for the cookie
    @Transactional
    public String issue(HrUser user) {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        repository.save(new RefreshToken(user, hash(raw), LocalDateTime.now().plus(lifetime)));
        return raw;
    }

    public record Rotation(HrUser user, String newToken) { }

    // swaps a valid token for a new one; empty if the token is unknown, expired, revoked or the user is disabled.
    // Runs in its own transaction so a detected reuse stays revoked even though the caller then returns 401.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<Rotation> rotate(String raw) {
        if (raw == null || raw.isBlank()) {
            return Optional.empty();
        }
        LocalDateTime now = LocalDateTime.now();
        Optional<RefreshToken> found = repository.findByTokenHash(hash(raw));
        if (found.isEmpty()) {
            return Optional.empty();
        }
        RefreshToken token = found.get();
        HrUser user = token.getUser();
        if (token.isRevoked() && token.getRevokedAt().isAfter(now.minus(REUSE_GRACE))) {
            return Optional.empty(); // two tabs refreshing at once: the loser just gets a 401, no alarm
        }
        if (token.isRevoked()) {
            log.warn("Reuse of a revoked refresh token for user {} -- revoking all of their sessions", user.getUsername());
            repository.revokeAllForUser(user.getId(), now);
            return Optional.empty();
        }
        if (token.isExpired(now) || !user.isEnabled()) {
            token.revoke(now);
            return Optional.empty();
        }
        token.revoke(now);
        return Optional.of(new Rotation(user, issue(user)));
    }

    @Transactional
    public void revoke(String raw) {
        if (raw != null && !raw.isBlank()) {
            repository.findByTokenHash(hash(raw)).ifPresent(t -> t.revoke(LocalDateTime.now()));
        }
    }

    @Transactional
    public void revokeAllFor(HrUser user) {
        repository.revokeAllForUser(user.getId(), LocalDateTime.now());
    }

    // housekeeping: tokens that expired more than a day ago are useless (called at login)
    @Transactional
    public void purgeExpired() {
        repository.deleteExpiredBefore(LocalDateTime.now().minusDays(1));
    }

    static String hash(String raw) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
