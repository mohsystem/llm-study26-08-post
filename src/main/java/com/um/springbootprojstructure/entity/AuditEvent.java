package com.um.springbootprojstructure.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_events", indexes = {
        @Index(name = "idx_audit_events_actor", columnList = "actor"),
        @Index(name = "idx_audit_events_type", columnList = "event_type"),
        @Index(name = "idx_audit_events_created_at", columnList = "created_at")
})
public class AuditEvent {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "event_type", nullable = false, length = 80)
    private String eventType;

    /**
     * For anonymous registration, actor may be "ANONYMOUS" or null.
     */
    @Column(nullable = false, length = 120)
    private String actor;

    @Column(name = "target_id", columnDefinition = "uuid")
    private UUID targetId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }

    public AuditEvent() {}

    public AuditEvent(String eventType, String actor, UUID targetId) {
        this.eventType = eventType;
        this.actor = actor;
        this.targetId = targetId;
    }

    public UUID getId() { return id; }
    public String getEventType() { return eventType; }
    public String getActor() { return actor; }
    public UUID getTargetId() { return targetId; }
    public Instant getCreatedAt() { return createdAt; }
}
