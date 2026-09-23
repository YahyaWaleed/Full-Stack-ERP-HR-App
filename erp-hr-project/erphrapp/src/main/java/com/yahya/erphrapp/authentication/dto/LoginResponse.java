package com.yahya.erphrapp.authentication.dto;

// token = short-lived access token; the refresh token travels only in the HttpOnly cookie
public class LoginResponse {

    private String token;
    private String role;
    private String username;
    private long expiresInSeconds;

    public LoginResponse(String token, String role, String username, long expiresInSeconds) {
        this.token = token;
        this.role = role;
        this.username = username;
        this.expiresInSeconds = expiresInSeconds;
    }

    public String getToken() { return token; }
    public String getRole() { return role; }
    public String getUsername() { return username; }
    public long getExpiresInSeconds() { return expiresInSeconds; }
}
