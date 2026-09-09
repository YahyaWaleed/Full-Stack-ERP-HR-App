package com.yahya.erphrapp.organization.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public class JobTitleRequest {

   @NotBlank
    private String code;

    @NotBlank
    private String titleEn;

   @NotBlank
    private String titleAr;

    public enum JobGrade {G1, G2, G3, G4, G5, G6, G7};
    private String  jobGrade;

    private BigDecimal minSalary;

    private BigDecimal maxSalary;

    private boolean isManagerial;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getTitleAr() {
        return titleAr;
    }

    public void setTitleAr(String titleAr) {
        this.titleAr = titleAr;
    }

    public String getJobGrade() {
        return jobGrade;
    }

    public void setJobGrade(String  jobGrade) {
        this.jobGrade = jobGrade;
    }

    public BigDecimal getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(BigDecimal minSalary) {
        this.minSalary = minSalary;
    }

    public BigDecimal getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(BigDecimal maxSalary) {
        this.maxSalary = maxSalary;
    }

    public boolean isManagerial() {
        return isManagerial;
    }

    public void setManagerial(boolean managerial) {
        isManagerial = managerial;
    }
}
