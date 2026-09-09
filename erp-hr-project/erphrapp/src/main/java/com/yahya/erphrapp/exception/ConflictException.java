package com.yahya.erphrapp.exception;
//throw this when the request is valid and the thing exists, but doing the action would break a business rule
// status code = 409 Conflict
public class ConflictException extends RuntimeException{
    public ConflictException(String message) {
        super(message);
    }
}
