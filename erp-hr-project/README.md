# ERP HR & Payroll — backend

Spring Boot 4.1 (Java 25) + MySQL 8. The React client lives in `../erp-hr-client`.

## Prerequisites

- JDK 25
- MySQL 8 (local install or `docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=... mysql:8.0 --log-bin-trust-function-creators=1`)
- Docker, to run the tests (Testcontainers)

The schema contains stored functions and triggers, so a MySQL server with binary logging on needs
`log_bin_trust_function_creators=1` (as above), or a user with the `SUPER`/`SYSTEM_VARIABLES_ADMIN` privilege.

## Configuration

Nothing secret is committed. Everything environment-specific comes from environment variables:

| Variable | Required | Purpose |
|---|---|---|
| `DB_PASSWORD` | yes | database password |
| `JWT_SECRET` | yes | HMAC key for signing tokens — at least 32 random bytes |
| `ADMIN_PASSWORD_HASH` | yes | BCrypt hash for the first `admin` login (see below) |
| `DB_URL` | prod only | JDBC URL (dev defaults to `localhost:3306/erp_hr`, created if missing) |
| `DB_USERNAME` | prod only | database user (dev defaults to `root`; use a least-privilege account in prod) |
| `APP_CORS_ORIGINS` | prod only | comma-separated frontend origins (dev defaults to the Vite ports) |
| `SPRING_PROFILES_ACTIVE` | no | `dev` (default) or `prod` |

Generate the admin hash once — the plain password never goes into any file:

```bash
docker run --rm httpd:2.4-alpine htpasswd -bnBC 10 "" 'choose-a-strong-password' | tr -d ':\n'
```

## Database schema (Flyway)

The schema is created and upgraded automatically on startup by Flyway:

- `src/main/resources/db/migration/V1__init_schema.sql` — tables, payroll procedures/functions/triggers, report views
- `src/main/resources/db/migration/V2__seed_reference_data.sql` — tax brackets, settings, organisation, components, leave types, `admin` user
- `src/main/resources/db/demo/V3__demo_data.sql` — 35 demo employees and four payroll runs (**dev and tests only**, never loaded by `prod`)

Never edit an applied migration; add `V4__<description>.sql` instead. Hibernate runs with `ddl-auto=validate`,
so the app refuses to start if an entity no longer matches the schema.

An `erp_hr` database created by the old hand-run `erp_hr_payroll.sql` has no Flyway history — drop it and let
Flyway recreate it.

## Run

```bash
export DB_PASSWORD=... JWT_SECRET=... ADMIN_PASSWORD_HASH='$2y$10$...'
./mvnw spring-boot:run
```

Log in as `admin` with the password you hashed. For production: `SPRING_PROFILES_ACTIVE=prod` plus all variables above.

The client reads its API location from `VITE_API_URL` (default `http://localhost:8080/api`).

## Tests

```bash
./mvnw test
```

Starts MySQL in Docker, applies all three migrations, and tests the payroll procedures directly
(`PayrollProcedureTest`), the auth status codes, and the main service rules.

## Load test

```bash
k6 run -e LOAD_TEST_USERNAME=admin -e LOAD_TEST_PASSWORD=... ../load-tests/load-test.js
```
