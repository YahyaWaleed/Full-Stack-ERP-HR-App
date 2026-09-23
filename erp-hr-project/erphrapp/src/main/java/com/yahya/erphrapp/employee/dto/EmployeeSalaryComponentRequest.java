package com.yahya.erphrapp.employee.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmployeeSalaryComponentRequest {

    @NotNull
    private Long compId;

    @PositiveOrZero
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal percentage;

    @NotNull
    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    @Size(max = 150)
    private String notes;

    public Long getCompId() { return compId; }
    public void setCompId(Long compId) { this.compId = compId; }

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
