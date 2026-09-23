package com.yahya.erphrapp.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit")
@PreAuthorize("hasRole('HR_ADMIN')")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    // newest first; optionally narrowed to one record, e.g. ?targetType=EMPLOYEE&targetId=12
    @GetMapping
    public Page<AuditEntryResponse> list(@RequestParam(required = false) String targetType,
                                         @RequestParam(required = false) String targetId,
                                         Pageable pageable) {
        return auditService.list(targetType, targetId, pageable).map(AuditEntryResponse::from);
    }
}
