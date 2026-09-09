package com.yahya.erphrapp.leaves.dto;

public class LeaveTypeResponse {

    private Long id;
    private String code;
    private String nameEn;
    private String nameAr;
    private int annualQuota;
    private boolean paid;
    private boolean affectsBalance;
    private int maxConsecutive;
    private boolean requiresAttachment;
    private String genderRestriction;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }

    public String getNameAr() { return nameAr; }
    public void setNameAr(String nameAr) { this.nameAr = nameAr; }

    public int getAnnualQuota() { return annualQuota; }
    public void setAnnualQuota(int annualQuota) { this.annualQuota = annualQuota; }

    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }

    public boolean isAffectsBalance() { return affectsBalance; }
    public void setAffectsBalance(boolean affectsBalance) { this.affectsBalance = affectsBalance; }

    public int getMaxConsecutive() { return maxConsecutive; }
    public void setMaxConsecutive(int maxConsecutive) { this.maxConsecutive = maxConsecutive; }

    public boolean isRequiresAttachment() { return requiresAttachment; }
    public void setRequiresAttachment(boolean requiresAttachment) { this.requiresAttachment = requiresAttachment; }

    public String getGenderRestriction() { return genderRestriction; }
    public void setGenderRestriction(String genderRestriction) { this.genderRestriction = genderRestriction; }
}