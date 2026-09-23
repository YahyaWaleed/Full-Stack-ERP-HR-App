package com.yahya.erphrapp.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<AuditEntry, Long> {

    Page<AuditEntry> findAllByOrderByOccurredAtDescIdDesc(Pageable pageable);

    Page<AuditEntry> findAllByTargetTypeAndTargetIdOrderByOccurredAtDescIdDesc(String targetType, String targetId, Pageable pageable);
}
