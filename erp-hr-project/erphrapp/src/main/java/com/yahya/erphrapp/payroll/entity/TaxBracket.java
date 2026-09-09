package com.yahya.erphrapp.payroll.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tax_brackets")
public class TaxBracket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bracket_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fiscal_year", nullable = false)
    private PayrollSetting payrollSetting;

    @Column(name = "from_amount", nullable = false)
    private BigDecimal fromAmount;

    @Column(name = "to_amount", nullable = false)
    private BigDecimal toAmount;

    @Column(name = "rate", nullable = false)
    private BigDecimal rate;

    public TaxBracket() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PayrollSetting getPayrollSetting() { return payrollSetting; }
    public void setPayrollSetting(PayrollSetting payrollSetting) { this.payrollSetting = payrollSetting; }

    public BigDecimal getFromAmount() { return fromAmount; }
    public void setFromAmount(BigDecimal fromAmount) { this.fromAmount = fromAmount; }

    public BigDecimal getToAmount() { return toAmount; }
    public void setToAmount(BigDecimal toAmount) { this.toAmount = toAmount; }

    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }
}