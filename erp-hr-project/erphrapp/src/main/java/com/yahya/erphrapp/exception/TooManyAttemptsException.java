package com.yahya.erphrapp.exception;

// thrown when a login is attempted while the username or client is locked out
// status code = 429 Too Many Requests (with a Retry-After header)
public class TooManyAttemptsException extends RuntimeException {

    private final long retryAfterSeconds;

    public TooManyAttemptsException(long retryAfterSeconds) {
        super("Too many failed login attempts. Try again in " + Math.max(1, retryAfterSeconds / 60) + " minute(s).");
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
