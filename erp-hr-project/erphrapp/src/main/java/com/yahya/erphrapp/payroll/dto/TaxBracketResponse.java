package com.yahya.erphrapp.payroll.dto;

import java.math.BigDecimal;

public class TaxBracketResponse {

    private Long id;
    private int fiscalYear;
    private BigDecimal fromAmount;
    private BigDecimal toAmount;
    private BigDecimal rate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getFiscalYear() { return fiscalYear; }
    public void setFiscalYear(int fiscalYear) { this.fiscalYear = fiscalYear; }

    public BigDecimal getFromAmount() { return fromAmount; }
    public void setFromAmount(BigDecimal fromAmount) { this.fromAmount = fromAmount; }

    public BigDecimal getToAmount() { return toAmount; }
    public void setToAmount(BigDecimal toAmount) { this.toAmount = toAmount; }

    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }
}