package com.yahya.erphrapp.leaves.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "public_holidays")
public class PublicHoliday {

    @Id
    @Column(name = "holiday_date")
    private LocalDate date;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Column(name = "name_ar", nullable = false)
    private String nameAr;

    protected PublicHoliday() {
    }

    public LocalDate getDate() { return date; }
    public String getNameEn() { return nameEn; }
    public String getNameAr() { return nameAr; }
}
