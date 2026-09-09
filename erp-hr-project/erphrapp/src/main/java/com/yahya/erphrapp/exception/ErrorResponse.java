package com.yahya.erphrapp.exception;

import java.time.LocalDateTime;
import java.util.List;


// this class holds the items that make up an Error Response message
public class ErrorResponse {

    // variables
    private LocalDateTime timestamp;
    private int status;
    private String errorCode;
    private String message;
    private String path;
    private List<String> fieldErrors;

    // no-argument constructor
    public ErrorResponse() {};

    // constructor
    public ErrorResponse(LocalDateTime timestamp, int status, String errorCode, String message, String path, List<String> fieldErrors) {
        this.timestamp = timestamp;
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.path = path;
        this.fieldErrors = fieldErrors;
    }

    // getters only (will get the values automatically from the GlobalExceptionHandler)

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public List<String> getFieldErrors() {
        return fieldErrors;
    }
}
