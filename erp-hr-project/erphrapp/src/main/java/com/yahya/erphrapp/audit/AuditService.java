package com.yahya.erphrapp.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// records who ran payroll, terminated someone, changed a salary, ... (review 9.9) and logs the same line (3.5).
// Runs inside the caller's transaction, so the audit row is saved only if the change itself commits.
@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);
    private static final int MAX_DETAILS = 500;

    private final AuditRepository auditRepository;

    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Transactional
    public void record(String action, String targetType, Object targetId, String details) {
        String actor = currentActor();
        String trimmed = details != null && details.length() > MAX_DETAILS ? details.substring(0, MAX_DETAILS) : details;
        auditRepository.save(new AuditEntry(actor, action, targetType, String.valueOf(targetId), trimmed));
        log.info("AUDIT actor={} action={} target={}#{} {}", actor, action, targetType, targetId, trimmed == null ? "" : trimmed);
    }

    @Transactional(readOnly = true)
    public Page<AuditEntry> list(String targetType, String targetId, Pageable pageable) {
        if (targetType != null && targetId != null) {
            return auditRepository.findAllByTargetTypeAndTargetIdOrderByOccurredAtDescIdDesc(targetType, targetId, pageable);
        }
        return auditRepository.findAllByOrderByOccurredAtDescIdDesc(pageable);
    }

    private static String currentActor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getName() != null ? auth.getName() : "system";
    }
}
