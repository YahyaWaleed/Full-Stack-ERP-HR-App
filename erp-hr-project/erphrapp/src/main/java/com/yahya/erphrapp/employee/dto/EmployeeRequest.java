package com.yahya.erphrapp.employee.dto;

import com.yahya.erphrapp.employee.entity.Employee;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

// enum fields are typed, so an unknown value ("gender": "X") is rejected with 400 before reaching the service;
// sizes and patterns mirror the column definitions in V1__init_schema.sql
public class EmployeeRequest {

    @NotBlank
    @Size(max = 120)
    private String fullNameAr;

    @NotBlank
    @Size(max = 120)
    private String fullNameEn;

    @NotNull
    private Employee.Gender gender;

    @NotNull
    @Past
    private LocalDate birthDate;

    @NotBlank
    @Pattern(regexp = "\\d{14}", message = "must be exactly 14 digits")
    private String nationalId;

    private Employee.MaritalStatus maritalStatus; // optional, defaults to SINGLE

    @Min(0)
    @Max(20)
    private int dependents;

    @Email
    @Size(max = 100)
    private String email;

    @Size(max = 20)
    @Pattern(regexp = "^$|^\\+?[0-9 ()-]{7,20}$", message = "must be a phone number")
    private String mobile;

    @Size(max = 200)
    private String address;

    @NotNull
    private LocalDate hireDate;

    @NotNull
    private Long deptId;

    @NotNull
    private Long jobId;

    @NotNull
    private Long branchId;

    private Long managerId; // optional — top-level employees have none

    @Size(max = 20)
    private String insuranceNo;

    @Size(max = 60)
    private String bankName;

    @Size(max = 34)
    @Pattern(regexp = "^[A-Za-z0-9 ]*$", message = "may only contain letters, digits and spaces")
    private String bankAccount;

    private Employee.PaymentMethod paymentMethod; // optional, defaults to BANK

    // updates only: the version the client loaded; a mismatch means someone else saved in between (409)
    private Integer version;

    @Valid
    private EmployeeContractRequest contract; // the first contract, required on create only

    public String getFullNameAr() { return fullNameAr; }
    public void setFullNameAr(String fullNameAr) { this.fullNameAr = fullNameAr; }

    public String getFullNameEn() { return fullNameEn; }
    public void setFullNameEn(String fullNameEn) { this.fullNameEn = fullNameEn; }

    public Employee.Gender getGender() { return gender; }
    public void setGender(Employee.Gender gender) { this.gender = gender; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }

    public Employee.MaritalStatus getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(Employee.MaritalStatus maritalStatus) { this.maritalStatus = maritalStatus; }

    public int getDependents() { return dependents; }
    public void setDependents(int dependents) { this.dependents = dependents; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }

    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }

    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }

    public String getInsuranceNo() { return insuranceNo; }
    public void setInsuranceNo(String insuranceNo) { this.insuranceNo = insuranceNo; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getBankAccount() { return bankAccount; }
    public void setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }

    public Employee.PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(Employee.PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public EmployeeContractRequest getContract() { return contract; }
    public void setContract(EmployeeContractRequest contract) { this.contract = contract; }
}
