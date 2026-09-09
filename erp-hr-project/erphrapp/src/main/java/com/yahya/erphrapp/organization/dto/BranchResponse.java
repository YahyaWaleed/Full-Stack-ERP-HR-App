package com.yahya.erphrapp.organization.dto;

import jakarta.validation.constraints.NotBlank;

public class BranchResponse {

    private Long id;

    private String code;

    private String nameEn;

    private String nameAr;

    private String city;

    private String country;

    private String address;

    private boolean isActive;

    public void setId(Long id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getNameAr() {
        return nameAr;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getAddress() {
        return address;
    }

    public boolean isActive() {
        return isActive;
    }
}
