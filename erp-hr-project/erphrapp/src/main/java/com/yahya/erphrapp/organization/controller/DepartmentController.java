package com.yahya.erphrapp.organization.controller;

import com.yahya.erphrapp.organization.dto.DepartmentResponse;
import com.yahya.erphrapp.organization.service.DepartmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    // inject the service class
    private final DepartmentService departmentService;
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    // get all departments
    @GetMapping
    public List<DepartmentResponse> getDepartments() {
        return departmentService.getDepartments();
    }

    // get one department
    @GetMapping("/{id}")
    public DepartmentResponse getDepartment(@PathVariable Long id) {
        return  departmentService.getDepartment(id);
    }
}
