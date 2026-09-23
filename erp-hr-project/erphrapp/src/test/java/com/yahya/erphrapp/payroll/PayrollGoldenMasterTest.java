package com.yahya.erphrapp.payroll;

import com.yahya.erphrapp.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

// Golden master for the set-based sp_run_payroll (V5): the old cursor version (V1, installed here as
// sp_run_payroll_legacy) and the new one must produce exactly the same payroll for the same inputs.
// Every test runs in a transaction that is rolled back.
@Transactional
class PayrollGoldenMasterTest extends AbstractIntegrationTest {

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeAll
    static void installLegacyProcedure(@Autowired JdbcTemplate jdbc) throws Exception {
        String sql = new ClassPathResource("db/legacy/sp_run_payroll_legacy.sql").getContentAsString(StandardCharsets.UTF_8);
        jdbc.execute("DROP PROCEDURE IF EXISTS sp_run_payroll_legacy");
        jdbc.execute(sql.substring(sql.indexOf("CREATE PROCEDURE")));
    }

    // a processed month with attendance, overtime, absence, pro-rating and loan installments
    @Test
    void processedDemoMonthMatches() {
        assertSameResult("2026-08");
    }

    // a new month with no attendance rows yet (the "no attendance" defaults) and loans that are one installment from closing
    @Test
    void newMonthWithoutAttendanceMatches() {
        jdbc.update("""
                INSERT INTO payroll_periods (period_code, fiscal_year, start_date, end_date, pay_date, working_days, status)
                VALUES ('2026-11', 2026, '2026-11-01', '2026-11-30', '2026-11-26', 21, 'OPEN')""");
        jdbc.update("UPDATE loans SET remaining_balance = LEAST(remaining_balance, monthly_installment / 2) WHERE status = 'ACTIVE'");
        assertSameResult("2026-11");
    }

    private void assertSameResult(String periodCode) {
        jdbc.execute("CALL sp_run_payroll_legacy('" + periodCode + "')");
        Snapshot legacy = snapshot(periodCode);

        jdbc.execute("CALL sp_run_payroll('" + periodCode + "')"); // re-run: undoes the legacy run first
        Snapshot setBased = snapshot(periodCode);

        assertThat(legacy.payslips()).isNotEmpty();
        assertThat(setBased.payslips()).isEqualTo(legacy.payslips());
        assertThat(setBased.lines()).isEqualTo(legacy.lines());
        assertThat(setBased.installments()).isEqualTo(legacy.installments());
        assertThat(setBased.loans()).isEqualTo(legacy.loans());
    }

    private record Snapshot(List<Map<String, Object>> payslips, List<Map<String, Object>> lines,
                            List<Map<String, Object>> installments, List<Map<String, Object>> loans) { }

    // everything except generated ids and timestamps, in a stable order
    private Snapshot snapshot(String periodCode) {
        return new Snapshot(
                jdbc.queryForList("""
                        SELECT p.payslip_no, p.emp_id, p.basic_salary, p.total_earnings, p.total_deductions, p.taxable_income,
                               p.income_tax, p.insurance_employee, p.insurance_employer, p.net_pay, p.worked_days,
                               p.absent_days, p.overtime_hours, p.status
                          FROM payslips p JOIN payroll_periods pp ON pp.period_id = p.period_id
                         WHERE pp.period_code = ? ORDER BY p.emp_id""", periodCode),
                jdbc.queryForList("""
                        SELECT p.emp_id, l.comp_code, l.comp_type, l.amount, l.is_taxable, l.calc_note, l.print_order
                          FROM payslip_lines l JOIN payslips p ON p.payslip_id = l.payslip_id
                          JOIN payroll_periods pp ON pp.period_id = p.period_id
                         WHERE pp.period_code = ? ORDER BY p.emp_id, l.print_order, l.comp_code, l.calc_note""", periodCode),
                jdbc.queryForList("""
                        SELECT li.loan_id, p.emp_id, li.amount FROM loan_installments li
                          JOIN payslips p ON p.payslip_id = li.payslip_id
                         WHERE li.period_code = ? ORDER BY li.loan_id""", periodCode),
                jdbc.queryForList("SELECT loan_id, remaining_balance, status FROM loans ORDER BY loan_id"));
    }
}
