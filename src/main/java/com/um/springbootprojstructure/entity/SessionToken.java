package com.um.springbootprojstructure.entity;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.Instant;
import java.util.UUID;

@Entity
@Audited // revision history for security-sensitive entity
@Table(
        name = "session_tokens",
        indexes = {
                @Index(name = "idx_session_tokens_user_id", columnList = "user_id"),
                @Index(name = "idx_session_tokens_expires_at", columnList = "expires_at"),
                @Index(name = "idx_session_tokens_token_hash", columnList = "token_hash", unique = true)
        }
)
public class SessionToken {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private UserAccount user;

    /**
     * Store ONLY a hash of the token to reduce data exposure if DB is leaked.
     */
    @Column(name = "token_hash", nullable = false, length = 64, updatable = false)
    private String tokenHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false, updatable = false)
    private Instant expiresAt;

    @Column(name = "revoked", nullable = false)
    private boolean revoked = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "mfa_status", nullable = false, length = 20)
    private MfaStatus mfaStatus = MfaStatus.PENDING;

    @Column(name = "mfa_verified_at")
    private Instant mfaVerifiedAt;

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }

    public UUID getId() { return id; }

    public UserAccount getUser() { return user; }
    public void setUser(UserAccount user) { this.user = user; }

    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }

    public Instant getCreatedAt() { return createdAt; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public boolean isRevoked() { return revoked; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }

    public MfaStatus getMfaStatus() { return mfaStatus; }
    public void setMfaStatus(MfaStatus mfaStatus) { this.mfaStatus = mfaStatus; }

    public Instant getMfaVerifiedAt() { return mfaVerifiedAt; }
    public void setMfaVerifiedAt(Instant mfaVerifiedAt) { this.mfaVerifiedAt = mfaVerifiedAt; }
}
