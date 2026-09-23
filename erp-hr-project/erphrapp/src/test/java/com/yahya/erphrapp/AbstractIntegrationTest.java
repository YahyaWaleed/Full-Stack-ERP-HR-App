package com.yahya.erphrapp;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

    static final MySQLContainer<?> mysql;

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
    }
}
