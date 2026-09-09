package com.yahya.erphrapp.report.dto;

import java.math.BigDecimal;

public class HeadcountByDeptResponse {
    private String code, department, branch;
    private Long headcount, males, females;
    private BigDecimal avgServiceYears, avgBasicSalary;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
    public Long getHeadcount() { return headcount; }
    public void setHeadcount(Long headcount) { this.headcount = headcount; }
    public Long getMales() { return males; }
    public void setMales(Long males) { this.males = males; }
    public Long getFemales() { return females; }
    public void setFemales(Long females) { this.females = females; }
    public BigDecimal getAvgServiceYears() { return avgServiceYears; }
    public void setAvgServiceYears(BigDecimal avgServiceYears) { this.avgServiceYears = avgServiceYears; }
    public BigDecimal getAvgBasicSalary() { return avgBasicSalary; }
    public void setAvgBasicSalary(BigDecimal avgBasicSalary) { this.avgBasicSalary = avgBasicSalary; }
}