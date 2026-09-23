package com.yahya.erphrapp.authentication.security;

import com.yahya.erphrapp.exception.TooManyAttemptsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Brute-force protection for /auth/login (review 9.6). Failures are counted per username and per client IP;
// after maxAttempts failures within the window, that key is locked for lockDuration. A successful login clears
// the username's counter. In memory, so it is per instance -- enough for a single-node deployment.
@Service
public class LoginAttemptService {

    private final int maxAttempts;
    private final Duration window;
    private final Duration lockDuration;
    private final Clock clock;
    private final Map<String, Attempts> attempts = new ConcurrentHashMap<>();

    @Autowired
    public LoginAttemptService(@Value("${app.auth.login.max-attempts:5}") int maxAttempts,
                               @Value("${app.auth.login.window-minutes:15}") long windowMinutes,
                               @Value("${app.auth.login.lock-minutes:15}") long lockMinutes) {
        this(maxAttempts, Duration.ofMinutes(windowMinutes), Duration.ofMinutes(lockMinutes), Clock.systemUTC());
    }

    LoginAttemptService(int maxAttempts, Duration window, Duration lockDuration, Clock clock) {
        this.maxAttempts = maxAttempts;
        this.window = window;
        this.lockDuration = lockDuration;
        this.clock = clock;
    }

    // throws 429 if either the username or the client is currently locked out
    public void checkAllowed(String username, String clientIp) {
        Instant now = clock.instant();
        long wait = Math.max(secondsLocked(userKey(username), now), secondsLocked(ipKey(clientIp), now));
        if (wait > 0) {
            throw new TooManyAttemptsException(wait);
        }
    }

    public void recordFailure(String username, String clientIp) {
        Instant now = clock.instant();
        fail(userKey(username), now);
        // an IP gets more room than a username so a shared office network isn't locked out by one person
        fail(ipKey(clientIp), now, maxAttempts * 4);
    }

    public void recordSuccess(String username) {
        attempts.remove(userKey(username));
    }

    private void fail(String key, Instant now) {
        fail(key, now, maxAttempts);
    }

    private void fail(String key, Instant now, int limit) {
        attempts.compute(key, (k, a) -> {
            Attempts current = (a == null || a.windowStart.plus(window).isBefore(now)) ? new Attempts(now) : a;
            current.count++;
            if (current.count >= limit) {
                current.lockedUntil = now.plus(lockDuration);
            }
            return current;
        });
        if (attempts.size() > 10_000) {
            attempts.entrySet().removeIf(e -> e.getValue().isStale(now, window));
        }
    }

    private long secondsLocked(String key, Instant now) {
        Attempts a = attempts.get(key);
        if (a == null || a.lockedUntil == null || !a.lockedUntil.isAfter(now)) {
            return 0;
        }
        return Duration.between(now, a.lockedUntil).toSeconds() + 1;
    }

    private static String userKey(String username) {
        return "u:" + (username == null ? "" : username.trim().toLowerCase(Locale.ROOT));
    }

    private static String ipKey(String ip) {
        return "ip:" + (ip == null ? "" : ip);
    }

    private static final class Attempts {
        final Instant windowStart;
        int count;
        Instant lockedUntil;

        Attempts(Instant windowStart) {
            this.windowStart = windowStart;
        }

        boolean isStale(Instant now, Duration window) {
            return windowStart.plus(window).isBefore(now) && (lockedUntil == null || lockedUntil.isBefore(now));
        }
    }
}
