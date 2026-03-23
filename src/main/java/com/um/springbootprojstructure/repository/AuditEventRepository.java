package com.um.springbootprojstructure.repository;

import com.um.springbootprojstructure.entity.AuditEvent;
import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.UUID;

public interface AuditEventRepository extends Repository<AuditEvent, UUID> {

    @PreAuthorize("permitAll()") // service controls what is written; no controller should call this
    AuditEvent save(AuditEvent event);
}
