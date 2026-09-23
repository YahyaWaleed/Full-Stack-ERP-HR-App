import http from 'k6/http';
import { check, sleep } from 'k6';

// Run against a database seeded with seed-load-data.sql (5,000+ employees, 12 payroll periods) --
// with 35 demo employees every endpoint is fast and the numbers say nothing.
//
//   k6 run -e LOAD_TEST_USERNAME=admin -e LOAD_TEST_PASSWORD=... load-test.js
//   (optional: -e BASE_URL=http://localhost:8080  -e RUN_PERIOD=2026-09  to also time one payroll run)

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const API = `${BASE_URL}/api/v1`;

// credentials are never committed -- pass them in with -e
const USERNAME = __ENV.LOAD_TEST_USERNAME;
const PASSWORD = __ENV.LOAD_TEST_PASSWORD;

export const options = {
    scenarios: {
        // HR staff browsing lists and detail pages
        browse: {
            executor: 'ramping-vus',
            exec: 'browse',
            stages: [
                { duration: '1m', target: 10 },
                { duration: '1m', target: 25 },
                { duration: '1m', target: 50 },
                { duration: '30s', target: 0 },
            ],
        },
        // the heavy queries: register, directory, trend
        reports: {
            executor: 'constant-vus',
            exec: 'reports',
            vus: 5,
            duration: '3m',
        },
        // writes: new hires at a steady trickle
        writes: {
            executor: 'constant-arrival-rate',
            exec: 'hire',
            rate: 2,
            timeUnit: '1s',
            duration: '3m',
            preAllocatedVUs: 5,
        },
    },
    thresholds: {
        'http_req_duration{kind:browse}': ['p(95)<500'],
        'http_req_duration{kind:reports}': ['p(95)<2000'],
        'http_req_duration{kind:writes}': ['p(95)<1000'],
        http_req_failed: ['rate<0.01'],
    },
};

export function setup() {
    if (!USERNAME || !PASSWORD) {
        throw new Error('Set LOAD_TEST_USERNAME and LOAD_TEST_PASSWORD (k6 run -e ...).');
    }
    const login = http.post(`${API}/auth/login`, JSON.stringify({ username: USERNAME, password: PASSWORD }),
        { headers: { 'Content-Type': 'application/json' } });
    if (login.status !== 200) {
        throw new Error(`Login failed (${login.status}): ${login.body}`);
    }
    const token = login.json().token;
    const params = auth(token);

    const periods = http.get(`${API}/payroll-periods`, params).json();
    const employees = http.get(`${API}/employees?size=200`, params).json().content;

    // optional: time one full payroll run over every employee (needs an OPEN period)
    if (__ENV.RUN_PERIOD) {
        const run = http.post(`${API}/payroll-periods/${__ENV.RUN_PERIOD}/run`, null, params);
        console.log(`PAYROLL RUN ${__ENV.RUN_PERIOD}: ${run.status} in ${run.timings.duration} ms`);
    }

    return {
        token,
        periodCodes: periods.filter((p) => p.status !== 'OPEN').map((p) => p.periodCode),
        employeeIds: employees.map((e) => e.id),
    };
}

function auth(token) {
    return { headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } };
}

function pick(list) {
    return list[Math.floor(Math.random() * list.length)];
}

function ok(res, name) {
    check(res, { [`${name} 200`]: (r) => r.status === 200 });
}

export function browse(data) {
    const params = { ...auth(data.token), tags: { kind: 'browse' } };
    const page = Math.floor(Math.random() * 50);
    ok(http.get(`${API}/employees?page=${page}&size=25`, params), 'employees page');
    ok(http.get(`${API}/employees?q=load&deptId=${1 + (page % 10)}`, params), 'employee search');
    ok(http.get(`${API}/employees/${pick(data.employeeIds)}`, params), 'employee');
    ok(http.get(`${API}/attendance?page=${page}&size=25`, params), 'attendance');
    ok(http.get(`${API}/leaves?size=25`, params), 'leaves');
    ok(http.get(`${API}/loans?size=25`, params), 'loans');
    ok(http.get(`${API}/dashboard/summary`, params), 'dashboard');
    ok(http.get(`${API}/payroll-periods/${pick(data.periodCodes)}/payslips?size=25`, params), 'payslips');
    sleep(1);
}

export function reports(data) {
    const params = { ...auth(data.token), tags: { kind: 'reports' } };
    const period = pick(data.periodCodes);
    ok(http.get(`${API}/reports/payroll-register?periodCode=${period}`, params), 'payroll register');
    ok(http.get(`${API}/reports/employee-directory?page=0&size=100`, params), 'employee directory');
    ok(http.get(`${API}/reports/headcount-by-department`, params), 'headcount');
    ok(http.get(`${API}/reports/payroll-trend`, params), 'payroll trend');
    ok(http.get(`${API}/reports/payroll-cost-by-department?periodCode=${period}`, params), 'cost by dept');
    sleep(2);
}

let hireCounter = 0;

export function hire(data) {
    const params = { ...auth(data.token), tags: { kind: 'writes' } };
    hireCounter += 1;
    // 13 digits, unique per VU and per run (national IDs must not repeat across runs either)
    const unique = `${Date.now() % 1e10}`.padStart(10, '0') + `${__VU}`.padStart(3, '0');
    const body = {
        fullNameEn: `k6 Hire ${unique}`,
        fullNameAr: 'موظف k6',
        gender: hireCounter % 2 ? 'M' : 'F',
        birthDate: '1996-04-04',
        nationalId: `4${unique}`,
        hireDate: '2026-09-01',
        deptId: 4,
        jobId: 30,
        branchId: 1,
        contract: { contractType: 'PERMANENT', startDate: '2026-09-01', basicSalary: 12000 },
    };
    const res = http.post(`${API}/employees`, JSON.stringify(body), params);
    check(res, { 'hire 200': (r) => r.status === 200 });
}
