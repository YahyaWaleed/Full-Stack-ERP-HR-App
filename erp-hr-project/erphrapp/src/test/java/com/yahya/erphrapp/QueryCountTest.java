package com.yahya.erphrapp;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

// N+1 guard (review 7.1): a list endpoint must run a fixed number of SQL statements no matter how many
// rows it returns. With lazy associations loaded one by one, 25 employees would cost 1 + 4x25 queries.
class QueryCountTest extends AbstractIntegrationTest {

    private static final int MAX_STATEMENTS = 5; // data query + count query + auth/cache lookups

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/v1/employees?size=25", "/api/v1/employees?managerial=true", "/api/v1/leaves?size=25",
            "/api/v1/loans?size=25", "/api/v1/contracts?size=25", "/api/v1/attendance?size=25",
            "/api/v1/payroll-periods/2026-07/payslips?size=25", "/api/v1/payroll-periods/2026-07/payments",
            "/api/v1/employees/2/payslips", "/api/v1/employees/2/leave-balances", "/api/v1/employees/2/salary-components",
            "/api/v1/tax-brackets", "/api/v1/departments"
    })
    void listEndpointUsesAConstantNumberOfQueries(String path) {
        Statistics stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        get(path, ADMIN); // warm up caches so only the data queries are measured

        stats.clear();
        Response r = get(path, ADMIN);
        long statements = stats.getPrepareStatementCount();

        assertThat(r.status()).isEqualTo(200);
        assertThat(statements).as(path + " ran " + statements + " SQL statements").isLessThanOrEqualTo(MAX_STATEMENTS);
    }
}
