package com.um.springbootprojstructure.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.Instant;
import java.util.UUID;

@Entity
@Audited // security-sensitive PII access should be auditable
@Table(
        name = "identity_documents",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_identity_documents_user_id", columnNames = "user_id")
        },
        indexes = {
                @Index(name = "idx_identity_documents_user_id", columnList = "user_id")
        }
)
public class IdentityDocument {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private UserAccount user;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "content_type", nullable = false, length = 120)
    private String contentType;

    /**
     * Sensitive: never serialize.
     * Stored as BLOB.
     */
    @JsonIgnore
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "content", nullable = false)
    private byte[] content;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private Instant uploadedAt;

    @PrePersist
    void prePersist() {
        uploadedAt = Instant.now();
    }

    public UUID getId() { return id; }

    public UserAccount getUser() { return user; }
    public void setUser(UserAccount user) { this.user = user; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public byte[] getContent() { return content; }
    public void setContent(byte[] content) { this.content = content; }

    public Instant getUploadedAt() { return uploadedAt; }
}
