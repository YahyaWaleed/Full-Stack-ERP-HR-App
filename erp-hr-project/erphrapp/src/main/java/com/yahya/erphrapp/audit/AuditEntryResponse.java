package com.yahya.erphrapp.audit;

import java.time.LocalDateTime;

public record AuditEntryResponse(Long id, LocalDateTime occurredAt, String actor, String action,
                                 String targetType, String targetId, String details) {

    static AuditEntryResponse from(AuditEntry e) {
        return new AuditEntryResponse(e.getId(), e.getOccurredAt(), e.getActor(), e.getAction(),
                e.getTargetType(), e.getTargetId(), e.getDetails());
    }
}
