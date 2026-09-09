package com.yahya.erphrapp.report.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmployeeDirectoryResponse {
    private String empCode, fullNameAr, fullNameEn, gender, department, jobTitle, jobGrade, branch, manager, empStatus, contractType, email, mobile;
    private Integer age, yearsOfService;
    private LocalDate hireDate;
    private BigDecimal basicSalary;

    public String getEmpCode() { return empCode; }
    public void setEmpCode(String empCode) { this.empCode = empCode; }
    public String getFullNameAr() { return fullNameAr; }
    public void setFullNameAr(String fullNameAr) { this.fullNameAr = fullNameAr; }
    public String getFullNameEn() { return fullNameEn; }
    public void setFullNameEn(String fullNameEn) { this.fullNameEn = fullNameEn; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getJobGrade() { return jobGrade; }
    public void setJobGrade(String jobGrade) { this.jobGrade = jobGrade; }
    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
    public String getManager() { return manager; }
    public void setManager(String manager) { this.manager = manager; }
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    public Integer getYearsOfService() { return yearsOfService; }
    public void setYearsOfService(Integer yearsOfService) { this.yearsOfService = yearsOfService; }
    public String getEmpStatus() { return empStatus; }
    public void setEmpStatus(String empStatus) { this.empStatus = empStatus; }
    public String getContractType() { return contractType; }
    public void setContractType(String contractType) { this.contractType = contractType; }
    public BigDecimal getBasicSalary() { return basicSalary; }
    public void setBasicSalary(BigDecimal basicSalary) { this.basicSalary = basicSalary; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
}