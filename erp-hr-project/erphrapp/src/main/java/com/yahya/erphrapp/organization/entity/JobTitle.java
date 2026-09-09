package com.yahya.erphrapp.organization.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "job_titles")
public class JobTitle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "title_en")
    private String titleEn;

    @Column(name = "title_ar")
    private String titleAr;

    public enum JobGrade {G1, G2, G3, G4, G5, G6, G7};
    @Enumerated(EnumType.STRING)
    @Column(name = "job_grade", nullable = false)
    private JobGrade jobGrade;

    @Column(name = "min_salary")
    private BigDecimal minSalary;

    @Column(name = "max_salary")
    private BigDecimal maxSalary;

    @Column(name = "is_managerial")
    private boolean isManagerial;

    public JobTitle() {}

    public JobTitle(String code, Long id, String titleEn, String titleAr, JobGrade jobGrade, BigDecimal minSalary, BigDecimal maxSalary, boolean isManagerial) {
        this.code = code;
        this.id = id;
        this.titleEn = titleEn;
        this.titleAr = titleAr;
        this.jobGrade = jobGrade;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.isManagerial = isManagerial;
    }

    public boolean isManagerial() {
        return isManagerial;
    }

    public void setManagerial(boolean managerial) {
        isManagerial = managerial;
    }

    public BigDecimal getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(BigDecimal maxSalary) {
        this.maxSalary = maxSalary;
    }

    public BigDecimal getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(BigDecimal minSalary) {
        this.minSalary = minSalary;
    }

    public JobGrade getJobGrade() {
        return jobGrade;
    }

    public void setJobGrade(JobGrade jobGrade) {
        this.jobGrade = jobGrade;
    }

    public String getTitleAr() {
        return titleAr;
    }

    public void setTitleAr(String titleAr) {
        this.titleAr = titleAr;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getId() {
        return id;
    }
}
