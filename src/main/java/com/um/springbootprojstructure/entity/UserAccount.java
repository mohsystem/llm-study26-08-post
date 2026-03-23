package com.um.springbootprojstructure.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.Instant;
import java.util.UUID;

@Entity
@Audited // revision history for security-sensitive entity
@Table(
        name = "user_accounts",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_accounts_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_user_accounts_email", columnNames = "email")
        },
        indexes = {
                @Index(name = "idx_user_accounts_username", columnList = "username"),
                @Index(name = "idx_user_accounts_email", columnList = "email")
        }
)
public class UserAccount {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, length = 60)
    private String username;

    @Column(nullable = false, length = 200)
    private String email;

    /**
     * Sensitive: never return to clients. Never accept via @RequestBody binding.
     */
    @JsonIgnore
    @Column(name = "password_hash", nullable = false, length = 120)
    private String passwordHash;

    /**
     * Security-relevant enum: store as STRING to avoid ordinal shift vulnerabilities.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserRole role = UserRole.USER;

    /**
     * Security-relevant enum: store as STRING.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(nullable = false)
    private boolean active = true;

    /**
     * Public identifier (safe to expose in URLs).
     * This is not a secret, but avoids exposing internal primary keys.
     */
    @Column(name = "public_ref", nullable = false, unique = true, length = 36, updatable = false)
    private String publicRef;

    @Column(name = "phone_number", length = 32)
    private String phoneNumber;

    /**
     * System-managed timestamps. Not intended to be set by API input.
     * insertable/updatable kept true here because app sets them in callbacks,
     * but they are not exposed through DTOs (prevents mass assignment).
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (publicRef == null) {
            publicRef = UUID.randomUUID().toString();
        }
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    // getters/setters

    public UUID getId() { return id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public String getPublicRef() { return publicRef; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
