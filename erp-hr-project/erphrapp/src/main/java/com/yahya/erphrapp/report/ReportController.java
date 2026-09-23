package com.yahya.erphrapp.report;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// reports expose salaries and bank data -- HR_ADMIN only
@PreAuthorize("hasRole('HR_ADMIN')")
@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // the catalogue: slug, title, parameters -- the frontend builds its report menu and forms from this
    @GetMapping
    public List<ReportDefinitionResponse> list() {
        return reportService.list().stream().map(ReportDefinitionResponse::from).toList();
    }

    // e.g. GET /reports/payroll-register?periodCode=2026-07  or  /reports/employee-directory?page=0&size=50
    @GetMapping("/{slug}")
    public Object run(@PathVariable String slug,
                      @RequestParam Map<String, String> params,
                      @RequestParam(required = false) Integer page,
                      @RequestParam(required = false) Integer size) {
        Map<String, String> reportParams = new HashMap<>(params);
        reportParams.remove("page");
        reportParams.remove("size");
        return reportService.run(slug, reportParams, page, size);
    }

    public record ReportDefinitionResponse(String slug, String title, List<ReportDefinition.Param> params, boolean pageable) {
        static ReportDefinitionResponse from(ReportDefinition d) {
            return new ReportDefinitionResponse(d.slug(), d.title(), d.params(), d.pageable());
        }
    }
}
