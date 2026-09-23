package com.yahya.erphrapp.report;

import com.yahya.erphrapp.exception.BadRequestException;
import com.yahya.erphrapp.exception.ResourceNotFoundException;
import com.yahya.erphrapp.report.ReportDefinition.Param;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// runs any report from the registry: validates parameters, executes the SQL and maps each row by column name
// (never by position, review 5.1) into camelCase JSON fields
@Service
public class ReportService {

    private static final int MAX_PAGE_SIZE = 500;

    private final ReportRegistry registry;

    @PersistenceContext
    private EntityManager entityManager;

    public ReportService(ReportRegistry registry) {
        this.registry = registry;
    }

    public List<ReportDefinition> list() {
        return registry.all();
    }

    // rows as a plain list, or -- when page is given and the report supports it -- one page of rows
    @Transactional(readOnly = true)
    public Object run(String slug, Map<String, String> rawParams, Integer page, Integer size) {
        ReportDefinition report = registry.find(slug).orElseThrow(() -> new ResourceNotFoundException("Report", slug));
        Map<String, Object> params = bind(report, rawParams);

        Query query = entityManager.createNativeQuery(report.sql(), Tuple.class);
        params.forEach(query::setParameter);

        if (page == null || !report.pageable()) {
            return rows(query);
        }
        int pageSize = size == null ? 50 : Math.clamp(size, 1, MAX_PAGE_SIZE);
        int pageNumber = Math.max(page, 0);
        query.setFirstResult(pageNumber * pageSize).setMaxResults(pageSize);

        Query count = entityManager.createNativeQuery("SELECT COUNT(*) FROM (" + report.sql() + ") r");
        params.forEach(count::setParameter);
        long total = ((Number) count.getSingleResult()).longValue();
        return new ReportPage(rows(query), pageNumber, pageSize, total, (int) Math.ceil(total / (double) pageSize));
    }

    public record ReportPage(List<Map<String, Object>> content, int number, int size, long totalElements, int totalPages) { }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> rows(Query query) {
        List<Tuple> tuples = query.getResultList();
        return tuples.stream().map(ReportService::toRow).toList();
    }

    private static Map<String, Object> toRow(Tuple t) {
        Map<String, Object> row = new LinkedHashMap<>();
        for (TupleElement<?> element : t.getElements()) {
            row.put(camelCase(element.getAlias()), normalise(t.get(element)));
        }
        return row;
    }

    // JDBC hands back a mix of java.sql and wrapper types; make them serialise the same way the old DTOs did
    private static Object normalise(Object value) {
        return switch (value) {
            case null -> null;
            case java.sql.Date d -> d.toLocalDate();
            case java.sql.Timestamp ts -> ts.toLocalDateTime();
            case Character c -> String.valueOf(c);
            default -> value;
        };
    }

    static String camelCase(String column) {
        StringBuilder out = new StringBuilder();
        boolean upper = false;
        for (char ch : column.toLowerCase().toCharArray()) {
            if (ch == '_') {
                upper = true;
            } else {
                out.append(upper ? Character.toUpperCase(ch) : ch);
                upper = false;
            }
        }
        return out.toString();
    }

    private static Map<String, Object> bind(ReportDefinition report, Map<String, String> raw) {
        Map<String, Object> bound = new HashMap<>();
        for (Param p : report.params()) {
            String value = raw.get(p.name());
            if (value == null || value.isBlank()) {
                if (p.required()) {
                    throw new BadRequestException("Report '" + report.slug() + "' needs the parameter '" + p.name() + "'");
                }
                value = p.defaultValue();
            }
            bound.put(p.name(), value == null ? null : convert(p, value.trim()));
        }
        return bound;
    }

    private static Object convert(Param p, String value) {
        return switch (p.type()) {
            case TEXT -> value;
            case INTEGER -> {
                try {
                    yield Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new BadRequestException("Parameter '" + p.name() + "' must be a whole number");
                }
            }
            case PERIOD_CODE -> {
                if (!value.matches("\\d{4}-(0[1-9]|1[0-2])")) {
                    throw new BadRequestException("Parameter '" + p.name() + "' must be a period code like 2026-07");
                }
                yield value;
            }
        };
    }
}
