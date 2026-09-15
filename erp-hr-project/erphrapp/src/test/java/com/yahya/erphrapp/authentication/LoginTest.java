package com.yahya.erphrapp.authentication;

import com.yahya.erphrapp.AbstractIntegrationTest;
import com.yahya.erphrapp.authentication.dto.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestRestTemplate
class LoginTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void loginWithUnknownUsernameReturns401() {
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistent_user_xyz");
        request.setPassword("whatever");

        ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}