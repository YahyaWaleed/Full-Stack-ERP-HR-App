package com.yahya.erphrapp.payroll;

import com.yahya.erphrapp.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// calls the stored procedures directly, bypassing the Java checks, against the demo data from V3__demo_data.sql:
// 2026-05/06 CLOSED, 2026-07 PAID, 2026-08 PROCESSED (not paid). Every test rolls back.
@Transactional
class PayrollProcedureTest extends AbstractIntegrationTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void procedureRefusesToRerunAPaidPeriodAndKeepsItsPayments() {
        int paymentsBefore = countPayments("2026-07");
        assertThat(paymentsBefore).isPositive();

        assertThatThrownBy(() -> jdbc.execute("CALL sp_run_payroll('2026-07')"))
                .hasStackTraceContaining("Payroll can only be run on an OPEN or PROCESSED period");

        assertThat(countPayments("2026-07")).isEqualTo(paymentsBefore);
    }

    @Test
    void procedureRefusesToRerunWhenPaymentsExistEvenIfStatusWasReset() {
        // someone "reopens" a paid period by hand -- the payment check still protects the records
        jdbc.update("UPDATE payroll_periods SET status = 'PROCESSED' WHERE period_code = '2026-07'");
        int paymentsBefore = countPayments("2026-07");

        assertThatThrownBy(() -> jdbc.execute("CALL sp_run_payroll('2026-07')"))
                .hasStackTraceContaining("Payroll period already has payment records");

        assertThat(countPayments("2026-07")).isEqualTo(paymentsBefore);
    }

    @Test
    void rerunningAProcessedPeriodDoesNotReviveACancelledLoan() {
        // loan 1 took an installment in the 2026-08 run; cancel it, then re-run 2026-08
        BigDecimal balanceBefore = loanBalance(1);
        BigDecimal installment = jdbc.queryForObject(
                "SELECT amount FROM loan_installments WHERE loan_id = 1 AND period_code = '2026-08'", BigDecimal.class);
        jdbc.update("UPDATE loans SET status = 'CANCELLED' WHERE loan_id = 1");

        jdbc.execute("CALL sp_run_payroll('2026-08')");

        assertThat(jdbc.queryForObject("SELECT status FROM loans WHERE loan_id = 1", String.class))
                .isEqualTo("CANCELLED");
        // the old installment was rolled back and no new one was taken
        assertThat(loanBalance(1)).isEqualByComparingTo(balanceBefore.add(installment));
        assertThat(jdbc.queryForObject(
                "SELECT COUNT(*) FROM loan_installments WHERE loan_id = 1 AND period_code = '2026-08'", Integer.class))
                .isZero();
    }

    @Test
    void rerunProducesConsistentPayslips() {
        jdbc.execute("CALL sp_run_payroll('2026-08')");

        List<Map<String, Object>> rows = jdbc.queryForList("""
                SELECT p.payslip_no, p.total_earnings, p.total_deductions, p.net_pay,
                       (SELECT IFNULL(SUM(amount),0) FROM payslip_lines l
                         WHERE l.payslip_id = p.payslip_id AND l.comp_type = 'EARNING')   AS earning_lines,
                       (SELECT IFNULL(SUM(amount),0) FROM payslip_lines l
                         WHERE l.payslip_id = p.payslip_id AND l.comp_type = 'DEDUCTION') AS deduction_lines,
                       fn_income_tax(p.taxable_income, 2026) AS expected_tax, p.income_tax
                  FROM payslips p JOIN payroll_periods pp ON pp.period_id = p.period_id
                 WHERE pp.period_code = '2026-08'""");

        assertThat(rows).isNotEmpty();
        assertThat(jdbc.queryForObject("SELECT status FROM payroll_periods WHERE period_code = '2026-08'", String.class))
                .isEqualTo("PROCESSED");
        for (Map<String, Object> r : rows) {
            BigDecimal earnings = (BigDecimal) r.get("total_earnings");
            BigDecimal deductions = (BigDecimal) r.get("total_deductions");
            assertThat(earnings).as("earnings of %s", r.get("payslip_no")).isEqualByComparingTo((BigDecimal) r.get("earning_lines"));
            assertThat(deductions).as("deductions of %s", r.get("payslip_no")).isEqualByComparingTo((BigDecimal) r.get("deduction_lines"));
            assertThat((BigDecimal) r.get("net_pay")).as("net of %s", r.get("payslip_no")).isEqualByComparingTo(earnings.subtract(deductions));
            assertThat((BigDecimal) r.get("income_tax")).as("tax of %s", r.get("payslip_no")).isEqualByComparingTo((BigDecimal) r.get("expected_tax"));
        }
    }

    private int countPayments(String periodCode) {
        return jdbc.queryForObject("""
                SELECT COUNT(*) FROM payroll_payments pp
                  JOIN payslips p ON p.payslip_id = pp.payslip_id
                  JOIN payroll_periods per ON per.period_id = p.period_id
                 WHERE per.period_code = ?""", Integer.class, periodCode);
    }

    private BigDecimal loanBalance(int loanId) {
        return jdbc.queryForObject("SELECT remaining_balance FROM loans WHERE loan_id = ?", BigDecimal.class, loanId);
    }
}
