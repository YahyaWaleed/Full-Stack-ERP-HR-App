package com.yahya.erphrapp.leaves.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "leave_types")
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Column(name = "name_ar", nullable = false)
    private String nameAr;

    @Column(name = "annual_quota", nullable = false)
    private int annualQuota;

    @Column(name = "is_paid", nullable = false)
    private boolean paid;

    @Column(name = "affects_balance", nullable = false)
    private boolean affectsBalance;

    @Column(name = "max_consecutive", nullable = false)
    private int maxConsecutive;

    @Column(name = "requires_attachment", nullable = false)
    private boolean requiresAttachment;

    public enum GenderRestriction {
        ANY, M, F
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "gender_restriction", nullable = false)
    private GenderRestriction genderRestriction;

    public LeaveType() {};


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameAr() {
        return nameAr;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    public int getAnnualQuota() {
        return annualQuota;
    }

    public void setAnnualQuota(int annualQuota) {
        this.annualQuota = annualQuota;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public boolean isAffectsBalance() {
        return affectsBalance;
    }

    public void setAffectsBalance(boolean affectsBalance) {
        this.affectsBalance = affectsBalance;
    }

    public int getMaxConsecutive() {
        return maxConsecutive;
    }

    public void setMaxConsecutive(int maxConsecutive) {
        this.maxConsecutive = maxConsecutive;
    }

    public boolean isRequiresAttachment() {
        return requiresAttachment;
    }

    public void setRequiresAttachment(boolean requiresAttachment) {
        this.requiresAttachment = requiresAttachment;
    }

    public GenderRestriction getGenderRestriction() {
        return genderRestriction;
    }

    public void setGenderRestriction(GenderRestriction genderRestriction) {
        this.genderRestriction = genderRestriction;
    }
}
