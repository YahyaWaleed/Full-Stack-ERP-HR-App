package com.yahya.erphrapp.employee.repository;

import com.yahya.erphrapp.employee.entity.Employee;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

// filters for GET /employees?branchId=&deptId=&jobId=&status=&managerial=&q= -- each one is optional
public final class EmployeeSpecifications {

    private EmployeeSpecifications() {
    }

    public static Specification<Employee> filter(Long branchId, Long deptId, Long jobId,
                                                 Employee.EmployeeStatus status, Boolean managerial, String q) {
        Specification<Employee> spec = (root, query, cb) -> cb.conjunction();
        if (branchId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("branch").get("id"), branchId));
        }
        if (deptId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("department").get("id"), deptId));
        }
        if (jobId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("jobTitle").get("id"), jobId));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("empStatus"), status));
        }
        if (managerial != null) {
            // joins on job_titles.is_managerial instead of matching job-title text in the browser (review 7.5)
            spec = spec.and((root, query, cb) -> cb.equal(root.get("jobTitle").get("isManagerial"), managerial));
        }
        if (q != null && !q.isBlank()) {
            String like = "%" + q.trim().toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("fullNameEn")), like),
                    cb.like(root.get("fullNameAr"), like),
                    cb.like(cb.lower(root.get("empCode")), like),
                    cb.like(root.get("nationalId"), like)));
        }
        return spec;
    }
}
