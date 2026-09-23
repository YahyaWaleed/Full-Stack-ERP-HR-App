# ERP HR & Payroll — web client

React 19 + Vite. The API is in `../erp-hr-project`.

```bash
npm install
npm run dev        # http://localhost:5173, talks to http://localhost:8080/api/v1
npm test           # Vitest
npm run lint
npm run build
```

`VITE_API_URL` points a build at another API (default `http://localhost:8080/api/v1`). With
`VITE_API_URL=/api/v1` the dev server proxies `/api` to `localhost:8080`, the same way nginx does in Docker.

## Layout

```
src/
  App.jsx                 routes; every page is lazy-loaded (its own chunk)
  shared/
    api/client.js         fetch wrapper: access token, one silent refresh + retry on 401, ApiError
    api/useApi.js         cached, de-duplicated, abortable reads; invalidate(prefix) after writes
    api/tokenStorage.js   access token in memory only (refresh token is an HttpOnly cookie)
    components/           DataTable, Pagination, PeriodSelect, EmployeePicker, ErrorBoundary, Breadcrumbs, ...
  features/
    auth/                 AuthProvider / useAuth (single source of truth), ProtectedRoute, LoginPage
    dashboard/            app shell (nav + breadcrumbs), home overview, 404
    employees/ leaves/ loans/ payroll/ attendance/ organization/ audit/
                          each with its pages and an api.js for its writes
    reports/              one ReportPage for every report; columns in reportColumns.jsx
```

Roles: routes wrapped in `admin(...)` and nav sections marked `adminOnly` mirror the backend's
`@PreAuthorize("hasRole('HR_ADMIN')")`. A 401 means the session is over (log in again); a 403 only shows a
permission message.
