# ERP HR & Payroll — backend

Spring Boot 4.1 (Java 25) + MySQL 8. The React client lives in `../erp-hr-client`.

## Quick start (everything in Docker)

```bash
cp ../.env.example ../.env     # fill in the values (see Configuration)
docker compose up --build      # run from the repository root
```

App: http://localhost:3000 · API docs: http://localhost:8080/swagger-ui.html · log in as `admin`.

## Prerequisites (without Docker)

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
| `JWT_SECRET` | yes | HMAC key for signing access tokens — at least 32 random bytes |
| `ADMIN_PASSWORD_HASH` | yes | BCrypt hash for the first `admin` login (see below) |
| `DB_URL` | prod only | JDBC URL (dev defaults to `localhost:3306/erp_hr`, created if missing) |
| `DB_USERNAME` | prod only | database user (dev defaults to `root`; use a least-privilege account in prod) |
| `APP_CORS_ORIGINS` | prod only | comma-separated frontend origins (dev defaults to the Vite ports) |
| `APP_AUTH_COOKIE_SECURE` | no | `true` (default) sends the refresh cookie over HTTPS only; `false` in dev |
| `SPRING_PROFILES_ACTIVE` | no | `dev` (default) or `prod` |

Generate the admin hash once — the plain password never goes into any file:

```bash
docker run --rm httpd:2.4-alpine htpasswd -bnBC 10 "" 'choose-a-strong-password' | tr -d ':\n'
```

## Database schema (Flyway)

Created and upgraded automatically on startup:

| Migration | Contents |
|---|---|
| `db/migration/V1__init_schema.sql` | tables, payroll functions/triggers, report views |
| `db/migration/V2__seed_reference_data.sql` | tax brackets, settings, organisation, components, leave types, `admin` user |
| `db/migration/V4__review_hardening.sql` | `@Version` columns, DB-generated employee codes, audit log, refresh tokens, public holidays, leave attachments, contracts' original end date |
| `db/migration/V5__set_based_payroll.sql` | `sp_run_payroll` rewritten set-based (no per-employee cursor) |
| `db/demo/V3__demo_data.sql` | 35 demo employees and four payroll runs — **dev and tests only**, never loaded by `prod` |

Never edit an applied migration; add `V6__<description>.sql` instead. Hibernate runs with `ddl-auto=validate`,
so the app refuses to start if an entity no longer matches the schema.

## Run

```bash
export DB_PASSWORD=... JWT_SECRET=... ADMIN_PASSWORD_HASH='$2y$10$...'
./mvnw spring-boot:run
```

For production: `SPRING_PROFILES_ACTIVE=prod` plus all variables above.

### Running from IntelliJ against the Docker database

Instead of environment variables, the `dev` profile also reads `erphrapp/config/local.properties` if it exists
(git-ignored). To use the MySQL started by `docker compose` (port 3307) while running the backend in the IDE:

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/erp_hr?allowPublicKeyRetrieval=true&useSSL=false
spring.datasource.username=erp_app
spring.datasource.password=<DB_PASSWORD from .env>
jwt.secret=<JWT_SECRET from .env>
spring.flyway.placeholders.admin_password_hash=<ADMIN_PASSWORD_HASH from .env, with $$ written as $>
server.port=8081
```

Stop the Docker backend first (`docker compose stop backend frontend`), and run the client with
`VITE_API_URL=http://localhost:8081/api/v1` (e.g. in `erp-hr-client/.env.development.local`).

## API

Everything is under `/api/v1`. Lists are paginated (`?page=0&size=25&sort=field,desc`) and filtered with query
parameters, e.g. `GET /api/v1/employees?deptId=4&status=ACTIVE&q=ahmed`, `GET /api/v1/employees?managerial=true`.
Sub-resources are nested: `/employees/{id}/contracts`, `/employees/{id}/payslips`, `/payroll-periods/{code}/payslips`.
State changes are `POST /{resource}/{id}/{action}` (`/loans/5/close`, `/leaves/9/approve`, `/employees/7/terminate`).

Errors are JSON `{status, errorCode, message, fieldErrors}`: 400 invalid input, 401 not logged in (the client logs
out), 403 not allowed (the client stays logged in), 404, 409 business-rule conflict or stale `version`, 429 login
lockout. Unexpected errors return a reference id that matches the server log line — never the exception text.

### Authentication

- `POST /auth/login` returns a **15-minute access token** and sets an **HttpOnly, SameSite=Strict refresh cookie**
  (7 days, path `/api/v1/auth`). The client keeps the access token in memory only.
- `POST /auth/refresh` rotates the refresh cookie and returns a new access token. Re-using an already-rotated
  refresh token revokes all of that user's sessions.
- `POST /auth/logout` revokes the refresh token.
- 5 failed logins in 15 minutes lock that username for 15 minutes (429 + `Retry-After`).
- `POST /users/{username}/disable` (admin) blocks login and revokes every session of that user.

### Reports

`GET /api/v1/reports` lists the 15 reports with their parameters; `GET /api/v1/reports/{slug}?...` runs one.
To add a report, add one entry to `report/ReportRegistry.java` (SQL + parameters); column aliases become the
JSON field names. The frontend menu and parameter form are built from the list automatically.

### Audit log

Payroll runs/payments, period creation, hiring, edits, terminations, contract renewals, salary component changes,
loan and leave decisions and user disabling are recorded (who, what, which record, when) and logged.
`GET /api/v1/audit` (admin).

## Tests and checks

```bash
./mvnw verify     # tests (needs Docker) + Spotless + Checkstyle
./mvnw spotless:apply   # fix formatting / unused imports
```

- `ReadEndpointsTest`, `QueryCountTest` — every GET endpoint returns 200; list endpoints run a constant number of SQL statements (no N+1)
- `PayrollGoldenMasterTest` — the set-based payroll produces exactly what the old cursor version did
- `PayrollProcedureTest`, `LeaveRulesTest` (incl. a two-thread approval race), `EmployeeLifecycleTest`, `LoanStateTest`,
  `AuthTest`, `ReportsTest`, `AuditTrailTest`

Frontend: `cd ../erp-hr-client && npm test`.

## Load test

Seed a *dev* database with 5,000+ employees and 12 months of payroll first — N+1 queries only show up at volume:

```bash
mysql -h 127.0.0.1 -u root -p erp_hr < ../load-tests/seed-load-data.sql
k6 run -e LOAD_TEST_USERNAME=admin -e LOAD_TEST_PASSWORD=... ../load-tests/load-test.js
```

### Load test results

Measured 2026-10-01 on a laptop running the whole stack in Docker Compose (MySQL 8, backend, `dev` profile),
with k6 on the same machine, so client and server compete for CPU. No application code was changed for the test.

- **Dataset:** 5,035 employees (5,000 from `seed-load-data.sql` + 35 demo) and 12 payroll periods (40,409 payslips) at
  the start. The `hire` scenario adds about 360 employees per run, so later runs started at 5,396 and 5,757.
- **Load:** up to 50 browsing VUs ramped over 3.5 min, 5 report VUs, and 2 hires/s for 3 min (about 56 VUs at peak).

| Run | browse p95 / p99 (limit 500 ms) | reports p95 / p99 (limit 2000 ms) | writes p95 / p99 (limit 1000 ms) | requests (req/s) | errors |
|---|---|---|---|---|---|
| 1. as-is, 5,035 employees | 334 ms / n/a | 400 ms / n/a | 233 ms / n/a | 19,966 (94/s) | 0.00% |
| 2. with payroll run, 5,396 employees | 358 ms / 514 ms | 453 ms / 614 ms | 296 ms / 367 ms | 19,481 (90/s) | 0.00% |
| 3. as-is repeat, 5,757 employees | 384 ms / 546 ms | 440 ms / 618 ms | 309 ms / 407 ms | 19,257 (91/s) | 0.00% |

All thresholds passed in all three runs. Run 1 was exported before p99 was enabled.

- **Payroll run:** `POST /payroll-periods/2026-09/run` took **5.3 s** and produced 5,395 payslips. The `2026-09` period and
  its attendance rows were created before the run (period via the API, attendance via the same SQL as the seed script).

Raw k6 summaries are in `load-tests/results/`. To reproduce (fresh database, backend on port 8080):

```bash
docker compose down -v && docker compose up -d --build mysql backend
mysql -h 127.0.0.1 -P 3307 -u root -p erp_hr < ../load-tests/seed-load-data.sql
k6 run --summary-trend-stats="avg,med,max,p(90),p(95),p(99)" -e LOAD_TEST_USERNAME=admin -e LOAD_TEST_PASSWORD=... \
  -e RUN_PERIOD=2026-09 ../load-tests/load-test.js   # needs an OPEN 2026-09 period with attendance rows
```
