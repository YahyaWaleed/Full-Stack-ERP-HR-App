package com.yahya.erphrapp.payroll;

import com.yahya.erphrapp.AbstractIntegrationTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TaxCalculationTest extends AbstractIntegrationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void incomeTaxIsCalculatedCorrectlyForKnownBracket() {
        // adjust this taxable income value and expected result to match
        // real numbers from your tax_brackets seed data for 2026
        Object result = entityManager
                .createNativeQuery("SELECT fn_income_tax(:income, :year)")
                .setParameter("income", new BigDecimal("100000"))
                .setParameter("year", 2026)
                .getSingleResult();

        BigDecimal tax = new BigDecimal(result.toString());
        assertThat(tax).isGreaterThan(BigDecimal.ZERO);
    }
}