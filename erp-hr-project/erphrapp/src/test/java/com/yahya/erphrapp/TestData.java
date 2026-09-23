package com.yahya.erphrapp;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

// request bodies for the tests, as the frontend would send them (plain JSON maps)
public final class TestData {

    private TestData() {
    }

    public static Map<String, Object> contract(LocalDate start, LocalDate end) {
        Map<String, Object> c = new HashMap<>();
        c.put("contractType", end == null ? "PERMANENT" : "FIXED_TERM");
        c.put("startDate", start.toString());
        if (end != null) c.put("endDate", end.toString());
        c.put("basicSalary", 30000);
        return c;
    }

    // a valid new employee in department 4 / job 30 / branch 1 of the demo data
    public static Map<String, Object> employee(String nationalId, LocalDate hired, Map<String, Object> contract) {
        Map<String, Object> e = new HashMap<>();
        e.put("fullNameEn", "Test Employee " + nationalId);
        e.put("fullNameAr", "موظف تجريبي");
        e.put("gender", "F");
        e.put("birthDate", "1995-05-05");
        e.put("nationalId", nationalId);
        e.put("hireDate", hired.toString());
        e.put("deptId", 4);
        e.put("jobId", 30);
        e.put("branchId", 1);
        e.put("contract", contract);
        return e;
    }
}
