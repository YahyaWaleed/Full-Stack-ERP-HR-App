package com.yahya.erphrapp.authentication;

import com.yahya.erphrapp.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

// 401 vs 403, login lockout (9.6), refresh-token rotation, reuse detection, logout and disabled users (9.5)
class AuthTest extends AbstractIntegrationTest {

    private static final String PASSWORD = "correct-horse-battery";

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String username;

    @BeforeEach
    void createUser() {
        username = "auth" + unique();
        jdbc.update("INSERT INTO hr_users (username, password, role) VALUES (?, ?, 'HR_USER')",
                username, passwordEncoder.encode(PASSWORD));
    }

    // ---- status codes ---------------------------------------------------------------------------

    @Test
    void missingOrGarbageTokenReturns401() {
        assertThat(get("/api/v1/employees", null).status()).isEqualTo(401);
        assertThat(call(HttpMethod.GET, "/api/v1/employees", null, "not-a-real-token", null).status()).isEqualTo(401);
    }

    @Test
    void hrUserOnAdminOnlyEndpointReturns403AndOnSharedEndpoint200() {
        assertThat(get("/api/v1/reports/employee-directory", USER).status()).isEqualTo(403);
        assertThat(get("/api/v1/employees", USER).status()).isEqualTo(200);
    }

    @Test
    void unknownUserAndWrongPasswordGetTheSame401() {
        Response unknown = login("nobody-" + unique(), "whatever");
        Response wrong = login(username, "wrong");
        assertThat(unknown.status()).isEqualTo(401);
        assertThat(wrong.status()).isEqualTo(401);
        assertThat(unknown.json().get("message")).isEqualTo(wrong.json().get("message"));
    }

    // ---- lockout --------------------------------------------------------------------------------

    @Test
    void fiveFailuresLockTheUsernameWith429EvenForTheRightPassword() {
        for (int i = 0; i < 5; i++) {
            assertThat(login(username, "wrong").status()).isEqualTo(401);
        }
        Response locked = login(username, PASSWORD);
        assertThat(locked.status()).isEqualTo(429);
        assertThat(locked.headers().getFirst(HttpHeaders.RETRY_AFTER)).isNotBlank();
    }

    // ---- refresh tokens -------------------------------------------------------------------------

    @Test
    void loginSetsAnHttpOnlyRefreshCookieAndReturnsAShortLivedAccessToken() {
        Response login = login(username, PASSWORD);
        assertThat(login.status()).isEqualTo(200);
        assertThat(login.json().get("token")).isNotNull();
        assertThat(((Number) login.json().get("expiresInSeconds")).longValue()).isLessThanOrEqualTo(900);

        String setCookie = login.headers().getFirst(HttpHeaders.SET_COOKIE);
        assertThat(setCookie).contains("erp_refresh=").contains("HttpOnly").contains("SameSite=Strict").contains("Path=/api/v1/auth");
        assertThat(call(HttpMethod.GET, "/api/v1/employees", null, (String) login.json().get("token"), null).status()).isEqualTo(200);
    }

    @Test
    void refreshRotatesTheCookieAndTheOldOneStopsWorking() {
        String first = cookieOf(login(username, PASSWORD));

        Response refreshed = refresh(first);
        assertThat(refreshed.status()).isEqualTo(200);
        assertThat(refreshed.json().get("username")).isEqualTo(username);
        String second = cookieOf(refreshed);
        assertThat(second).isNotEqualTo(first);

        assertThat(refresh(first).status()).isEqualTo(401);   // already rotated
        assertThat(refresh(second).status()).isEqualTo(200);
    }

    @Test
    void reusingARotatedTokenAfterTheGracePeriodRevokesEverySession() {
        String first = cookieOf(login(username, PASSWORD));
        String second = cookieOf(refresh(first));
        // pretend the first token was rotated a minute ago (outside the 10-second two-tabs grace period)
        jdbc.update("""
                UPDATE refresh_tokens t JOIN hr_users u ON u.user_id = t.user_id
                   SET t.revoked_at = NOW() - INTERVAL 1 MINUTE
                 WHERE u.username = ? AND t.revoked_at IS NOT NULL""", username);

        assertThat(refresh(first).status()).isEqualTo(401);   // stolen copy
        assertThat(refresh(second).status()).isEqualTo(401);  // ...so the legitimate session is gone too
    }

    @Test
    void logoutRevokesTheRefreshToken() {
        String cookie = cookieOf(login(username, PASSWORD));
        Response logout = call(HttpMethod.POST, "/api/v1/auth/logout", null, null, "erp_refresh=" + cookie);
        assertThat(logout.status()).isEqualTo(204);
        assertThat(refresh(cookie).status()).isEqualTo(401);
    }

    @Test
    void disablingAUserBlocksLoginAndRefresh() {
        String cookie = cookieOf(login(username, PASSWORD));

        assertThat(post("/api/v1/users/" + username + "/disable", null, ADMIN).status()).isEqualTo(204);
        assertThat(login(username, PASSWORD).status()).isEqualTo(401);
        assertThat(refresh(cookie).status()).isEqualTo(401);

        assertThat(post("/api/v1/users/" + username + "/enable", null, ADMIN).status()).isEqualTo(204);
        assertThat(login(username, PASSWORD).status()).isEqualTo(200);
    }

    // ---- helpers --------------------------------------------------------------------------------

    private Response login(String user, String password) {
        return post("/api/v1/auth/login", Map.of("username", user, "password", password), null);
    }

    private Response refresh(String cookieValue) {
        return call(HttpMethod.POST, "/api/v1/auth/refresh", null, null, "erp_refresh=" + cookieValue);
    }

    private static String cookieOf(Response response) {
        List<String> cookies = response.headers().get(HttpHeaders.SET_COOKIE);
        assertThat(cookies).as("Set-Cookie on " + response.status()).isNotEmpty();
        String cookie = cookies.getFirst();
        return cookie.substring("erp_refresh=".length(), cookie.indexOf(';'));
    }
}
