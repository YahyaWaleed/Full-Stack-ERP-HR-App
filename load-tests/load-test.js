import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '1m', target: 10 },
        { duration: '1m', target: 25 },
        { duration: '1m', target: 50 },
        { duration: '1m', target: 0 },
    ],

    thresholds: {
        http_req_duration: ['p(95)<500'],
        http_req_failed: ['rate<0.01'],
    },
};

const BASE_URL = 'http://localhost:8080';

export function setup() {

    const loginResponse = http.post(
        `${BASE_URL}/api/auth/login`,
        JSON.stringify({
            username: 'Yahya',
            password: 'REMOVED',
        }),
        {
            headers: {
                'Content-Type': 'application/json',
            },
        }
    );

    console.log(`LOGIN -> Status: ${loginResponse.status}`);

    check(loginResponse, {
        'login status 200': (response) =>
            response.status === 200,
    });

    if (loginResponse.status !== 200) {
        console.log(`LOGIN -> Response: ${loginResponse.body}`);
        throw new Error('Login failed. Cannot run load test.');
    }

    const loginData = loginResponse.json();

    console.log(`Login successful. Role: ${loginData.role}`);

    return {
        token: loginData.token,
    };
}

export default function (data) {

    const params = {
        headers: {
            Authorization: `Bearer ${data.token}`,
        },
    };

    // GET EMPLOYEES
    const employeesResponse = http.get(
        `${BASE_URL}/api/employees`,
        params
    );

    check(employeesResponse, {
        'GET /employees status 200': (response) =>
            response.status === 200,
    });

    // GET ATTENDANCE
    const attendanceResponse = http.get(
        `${BASE_URL}/api/attendance`,
        params
    );

    check(attendanceResponse, {
        'GET /attendance status 200': (response) =>
            response.status === 200,
    });

    sleep(3);
}