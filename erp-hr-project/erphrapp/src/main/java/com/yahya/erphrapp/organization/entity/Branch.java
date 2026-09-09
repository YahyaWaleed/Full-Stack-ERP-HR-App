package com.yahya.erphrapp.organization.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "branches")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branch_id")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name ="name_ar")
    private String nameAr;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @Column(name = "address")
    private String address;

    @Column(name = "is_active")
    private boolean isActive;

    public Branch() {};

    public Branch(String code, String nameEn, String nameAr, String city, String country, String address, boolean isActive) {
        this.code = code;
        this.nameEn = nameEn;
        this.nameAr = nameAr;
        this.city = city;
        this.country = country;
        this.address = address;
        this.isActive = isActive;
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
}
