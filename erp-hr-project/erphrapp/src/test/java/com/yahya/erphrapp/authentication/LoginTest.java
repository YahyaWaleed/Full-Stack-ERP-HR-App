package com.yahya.erphrapp.authentication;

import com.yahya.erphrapp.AbstractIntegrationTest;
import com.yahya.erphrapp.authentication.dto.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class LoginTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    void loginWithUnknownUsernameReturns401() {
        RestTemplate restTemplate = new RestTemplate();

        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistent_user_xyz");
        request.setPassword("whatever");

        try {
            restTemplate.postForEntity(
                    "http://localhost:" + port + "/api/auth/login",
                    request,
                    String.class);
            org.junit.jupiter.api.Assertions.fail("Expected a 401 Unauthorized response");
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }
}