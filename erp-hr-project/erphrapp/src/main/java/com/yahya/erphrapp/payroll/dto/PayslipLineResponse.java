package com.yahya.erphrapp.payroll.dto;

import java.math.BigDecimal;

public class PayslipLineResponse {

    private Long id;
    private String compCode;
    private String compNameAr;
    private String compType;
    private BigDecimal amount;
    private boolean taxable;
    private String calcNote;
    private int printOrder;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompCode() { return compCode; }
    public void setCompCode(String compCode) { this.compCode = compCode; }

    public String getCompNameAr() { return compNameAr; }
    public void setCompNameAr(String compNameAr) { this.compNameAr = compNameAr; }

    public String getCompType() { return compType; }
    public void setCompType(String compType) { this.compType = compType; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public boolean isTaxable() { return taxable; }
    public void setTaxable(boolean taxable) { this.taxable = taxable; }

    public String getCalcNote() { return calcNote; }
    public void setCalcNote(String calcNote) { this.calcNote = calcNote; }

    public int getPrintOrder() { return printOrder; }
    public void setPrintOrder(int printOrder) { this.printOrder = printOrder; }
}