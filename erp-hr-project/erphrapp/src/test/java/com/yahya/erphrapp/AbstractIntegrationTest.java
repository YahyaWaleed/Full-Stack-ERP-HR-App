package com.yahya.erphrapp;

import com.yahya.erphrapp.authentication.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.MySQLContainer;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

    public static final String ADMIN = "HR_ADMIN";
    public static final String USER = "HR_USER";

    static final MySQLContainer<?> mysql;

    // unique national IDs etc. across all tests sharing the one database
    private static final AtomicLong SEQUENCE = new AtomicLong(System.nanoTime() % 1_000_000L);

    static {
        mysql = new MySQLContainer<>("mysql:8.0")
                .withDatabaseName("erp_hr")
                .withUsername("test")
                .withPassword("test")
                .withCommand("mysqld", "--log-bin-trust-function-creators=1");

        mysql.start(); // started once for the whole test run — never explicitly stopped
    }

    // Spring Boot's Flyway runs the real migrations (src/main/resources/db/...) against the container on startup,
    // and ddl-auto=validate then checks every entity against that schema
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.flyway.locations", () -> "classpath:db/migration,classpath:db/demo");
        // test-only values; nothing here is a real credential
        registry.add("spring.flyway.placeholders.admin_password_hash",
                () -> "$2a$10$testonlytestonlytestonuD8m0dXoQ0f9zv4lE0mAqQkVbq8hJ3a");
        registry.add("jwt.secret", () -> "test-only-jwt-secret-at-least-32-bytes-long-0123456789");
        registry.add("spring.jpa.properties.hibernate.generate_statistics", () -> "true");
    }

    @LocalServerPort
    protected int port;

    @Autowired
    protected JwtUtil jwtUtil;

    protected static long unique() {
        return SEQUENCE.incrementAndGet();
    }

    // 14-digit national ID nobody else in the test run uses
    protected static String uniqueNationalId() {
        return String.format("29%012d", unique());
    }

    // ---- HTTP helpers: real requests against the running app ------------------------------------

    public record Response(int status, String body, HttpHeaders headers) {
        public Map<String, Object> json() {
            return JsonParserFactory.getJsonParser().parseMap(body);
        }

        public List<Object> jsonList() {
            return JsonParserFactory.getJsonParser().parseList(body);
        }
    }

    protected Response call(HttpMethod method, String path, Object body, String role) {
        return call(method, path, body, role == null ? null : jwtUtil.generateToken("test-" + role.toLowerCase(), role), null);
    }

    protected Response call(HttpMethod method, String path, Object body, String bearerToken, String cookie) {
        RestClient.RequestBodySpec spec = RestClient.create()
                .method(method)
                .uri("http://localhost:" + port + path)
                .headers(h -> {
                    if (bearerToken != null) h.setBearerAuth(bearerToken);
                    if (cookie != null) h.add(HttpHeaders.COOKIE, cookie);
                });
        if (body != null) {
            spec.contentType(MediaType.APPLICATION_JSON).body(body);
        }
        return spec.exchange((req, res) -> {
            String text;
            try {
                text = new String(res.getBody().readAllBytes());
            } catch (IOException e) {
                text = "";
            }
            return new Response(res.getStatusCode().value(), text, res.getHeaders());
        });
    }

    protected Response get(String path, String role) {
        return call(HttpMethod.GET, path, null, role);
    }

    protected Response post(String path, Object body, String role) {
        return call(HttpMethod.POST, path, body, role);
    }
}
