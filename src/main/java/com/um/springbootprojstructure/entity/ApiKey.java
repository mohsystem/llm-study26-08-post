package com.um.springbootprojstructure.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.Instant;
import java.util.UUID;

@Entity
@Audited
@Table(
        name = "api_keys",
        indexes = {
                @Index(name = "idx_api_keys_owner_id", columnList = "owner_id"),
                @Index(name = "idx_api_keys_status", columnList = "status"),
                @Index(name = "idx_api_keys_key_hash", columnList = "key_hash", unique = true)
        }
)
public class ApiKey {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    /**
     * Owner of the key (user/service account).
     * Ownership filtering is enforced in repository queries.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false, updatable = false)
    private UserAccount owner;

    @Column(nullable = false, length = 120)
    private String name;

    /**
     * Sensitive: store only a hash of the API key secret.
     * Never serialize.
     */
    @JsonIgnore
    @Column(name = "key_hash", nullable = false, length = 64, updatable = false)
    private String keyHash;

    @Enumerated(EnumType.STRING) // security-relevant enum
    @Column(nullable = false, length = 20)
    private ApiKeyStatus status = ApiKeyStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }

    public UUID getId() { return id; }

    public UserAccount getOwner() { return owner; }
    public void setOwner(UserAccount owner) { this.owner = owner; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getKeyHash() { return keyHash; }
    public void setKeyHash(String keyHash) { this.keyHash = keyHash; }

    public ApiKeyStatus getStatus() { return status; }
    public void setStatus(ApiKeyStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }

    public Instant getRevokedAt() { return revokedAt; }
    public void setRevokedAt(Instant revokedAt) { this.revokedAt = revokedAt; }
}
