package com.yahya.erphrapp.exception;

// throw this when the request itself is invalid (bad dates, missing required value for this case, ...)
// status code = 400 Bad Request
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
