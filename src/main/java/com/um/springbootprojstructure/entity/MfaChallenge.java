package com.um.springbootprojstructure.entity;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.Instant;
import java.util.UUID;

@Entity
@Audited
@Table(
        name = "mfa_challenges",
        indexes = {
                @Index(name = "idx_mfa_challenges_session_id", columnList = "session_id"),
                @Index(name = "idx_mfa_challenges_expires_at", columnList = "expires_at")
        }
)
public class MfaChallenge {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false, updatable = false)
    private SessionToken session;

    @Column(name = "otp_hash", nullable = false, length = 64, updatable = false)
    private String otpHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false, updatable = false)
    private Instant expiresAt;

    @Column(name = "attempts", nullable = false)
    private int attempts = 0;

    @Column(name = "verified", nullable = false)
    private boolean verified = false;

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }

    public UUID getId() { return id; }

    public SessionToken getSession() { return session; }
    public void setSession(SessionToken session) { this.session = session; }

    public String getOtpHash() { return otpHash; }
    public void setOtpHash(String otpHash) { this.otpHash = otpHash; }

    public Instant getCreatedAt() { return createdAt; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
}
