package com.yahya.erphrapp.payroll.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payslip_lines")
public class PayslipLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payslip_id", nullable = false)
    private Payslip payslip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comp_id", nullable = false)
    private SalaryComponent salaryComponent;

    @Column(name = "comp_code", nullable = false)
    private String compCode;

    @Column(name = "comp_name_ar", nullable = false)
    private String compNameAr;

    public enum CompType {
        EARNING, DEDUCTION
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "comp_type", nullable = false)
    private CompType compType;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "is_taxable", nullable = false)
    private boolean taxable;

    @Column(name = "calc_note")
    private String calcNote;

    @Column(name = "print_order", nullable = false)
    private int printOrder;

    public PayslipLine() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Payslip getPayslip() { return payslip; }
    public void setPayslip(Payslip payslip) { this.payslip = payslip; }

    public SalaryComponent getSalaryComponent() { return salaryComponent; }
    public void setSalaryComponent(SalaryComponent salaryComponent) { this.salaryComponent = salaryComponent; }

    public String getCompCode() { return compCode; }
    public void setCompCode(String compCode) { this.compCode = compCode; }

    public String getCompNameAr() { return compNameAr; }
    public void setCompNameAr(String compNameAr) { this.compNameAr = compNameAr; }

    public CompType getCompType() { return compType; }
    public void setCompType(CompType compType) { this.compType = compType; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public boolean isTaxable() { return taxable; }
    public void setTaxable(boolean taxable) { this.taxable = taxable; }

    public String getCalcNote() { return calcNote; }
    public void setCalcNote(String calcNote) { this.calcNote = calcNote; }

    public int getPrintOrder() { return printOrder; }
    public void setPrintOrder(int printOrder) { this.printOrder = printOrder; }
}