package com.yahya.erphrapp.payroll.dto;

import java.math.BigDecimal;

public class SalaryComponentResponse {

    private Long id;
    private String code;
    private String nameEn;
    private String nameAr;
    private String compType;
    private String calcType;
    private BigDecimal defaultValue;
    private boolean taxable;
    private boolean insurable;
    private boolean recurring;
    private int printOrder;
    private boolean active;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }

    public String getNameAr() { return nameAr; }
    public void setNameAr(String nameAr) { this.nameAr = nameAr; }

    public String getCompType() { return compType; }
    public void setCompType(String compType) { this.compType = compType; }

    public String getCalcType() { return calcType; }
    public void setCalcType(String calcType) { this.calcType = calcType; }

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