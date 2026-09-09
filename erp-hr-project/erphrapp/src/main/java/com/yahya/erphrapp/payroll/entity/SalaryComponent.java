package com.yahya.erphrapp.payroll.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "salary_components")
public class SalaryComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comp_id")
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Column(name = "name_ar", nullable = false)
    private String nameAr;

    public enum CompType {
        EARNING, DEDUCTION
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "comp_type", nullable = false)
    private CompType compType;

    public enum CalcType {
        FIXED, PCT_BASIC, COMPUTED
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "calc_type", nullable = false)
    private CalcType calcType;

    @Column(name = "default_value", nullable = false)
    private BigDecimal defaultValue;

    @Column(name = "is_taxable", nullable = false)
    private boolean taxable;

    @Column(name = "is_insurable", nullable = false)
    private boolean insurable;

    @Column(name = "is_recurring", nullable = false)
    private boolean recurring;

    @Column(name = "print_order", nullable = false)
    private int printOrder;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    public SalaryComponent() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }

    public String getNameAr() { return nameAr; }
    public void setNameAr(String nameAr) { this.nameAr = nameAr; }

    public CompType getCompType() { return compType; }
    public void setCompType(CompType compType) { this.compType = compType; }

    public CalcType getCalcType() { return calcType; }
    public void setCalcType(CalcType calcType) { this.calcType = calcType; }

    public BigDecimal getDefaultValue() { return defaultValue; }
    public void setDefaultValue(BigDecimal defaultValue) { this.defaultValue = defaultValue; }

    public boolean isTaxable() { return taxable; }
    public void setTaxable(boolean taxable) { this.taxable = taxable; }

    public boolean isInsurable() { return insurable; }
    public void setInsurable(boolean insurable) { this.insurable = insurable; }

    public boolean isRecurring() { return recurring; }
    public void setRecurring(boolean recurring) { this.recurring = recurring; }

    public int getPrintOrder() { return printOrder; }
    public void setPrintOrder(int printOrder) { this.printOrder = printOrder; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}