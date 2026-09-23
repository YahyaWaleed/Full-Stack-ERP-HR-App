package com.yahya.erphrapp.authentication;

import com.yahya.erphrapp.AbstractIntegrationTest;
import com.yahya.erphrapp.authentication.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

// the frontend logs out only on 401 and shows a permission message on 403, so the two must never be mixed up
class AuthStatusCodeTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void missingTokenReturns401() {
        assertThat(get("/api/employees", null)).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void garbageTokenReturns401() {
        assertThat(get("/api/employees", "not-a-real-token")).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void hrUserOnAdminOnlyEndpointReturns403() {
        String hrUserToken = jwtUtil.generateToken("someone", "HR_USER");
        assertThat(get("/api/reports/employee-directory", hrUserToken)).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void hrUserOnSharedEndpointReturns200() {
        String hrUserToken = jwtUtil.generateToken("someone", "HR_USER");
        assertThat(get("/api/employees", hrUserToken)).isEqualTo(HttpStatus.OK);
    }

    private HttpStatusCode get(String path, String token) {
        HttpHeaders headers = new HttpHeaders();
        if (token != null) {
            headers.setBearerAuth(token);
        }
        try {
            return new RestTemplate()
                    .exchange("http://localhost:" + port + path, HttpMethod.GET, new HttpEntity<>(headers), String.class)
                    .getStatusCode();
        } catch (HttpClientErrorException ex) {
            return ex.getStatusCode();
        }
    }
}
