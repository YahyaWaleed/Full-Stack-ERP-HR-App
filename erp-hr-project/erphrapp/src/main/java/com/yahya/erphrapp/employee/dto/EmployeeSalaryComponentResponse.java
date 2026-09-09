package com.yahya.erphrapp.employee.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmployeeSalaryComponentResponse {

    private Long id;
    private String empCode;
    private String componentCode;
    private String componentName;
    private BigDecimal amount;
    private BigDecimal percentage;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private String notes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmpCode() { return empCode; }
    public void setEmpCode(String empCode) { this.empCode = empCode; }

    public String getComponentCode() { return componentCode; }
    public void setComponentCode(String componentCode) { this.componentCode = componentCode; }

    public String getComponentName() { return componentName; }
    public void setComponentName(String componentName) { this.componentName = componentName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getPercentage() { return percentage; }
    public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }

    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDate effectiveFrom) { this.effectiveFrom = effectiveFrom; }

    public LocalDate getEffectiveTo() { return effectiveTo; }
    public void setEffectiveTo(LocalDate effectiveTo) { this.effectiveTo = effectiveTo; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}