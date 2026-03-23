package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.DocumentUpdateResponse;
import com.um.springbootprojstructure.entity.AuditEvent;
import com.um.springbootprojstructure.entity.IdentityDocument;
import com.um.springbootprojstructure.entity.UserAccount;
import com.um.springbootprojstructure.repository.AuditEventRepository;
import com.um.springbootprojstructure.repository.IdentityDocumentWriteRepository;
import com.um.springbootprojstructure.repository.projection.DocumentIdView;
import com.um.springbootprojstructure.repository.projection.UserIdView;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class IdentityDocumentWriteServiceImpl implements IdentityDocumentWriteService {

    private final IdentityDocumentWriteRepository writeRepository;
    private final AuditEventRepository auditEventRepository;
    private final EntityManager em;

    public IdentityDocumentWriteServiceImpl(IdentityDocumentWriteRepository writeRepository,
                                           AuditEventRepository auditEventRepository,
                                           EntityManager em) {
        this.writeRepository = writeRepository;
        this.auditEventRepository = auditEventRepository;
        this.em = em;
    }

    /**
     * Security-sensitive write:
     * - @Transactional
     * - audit event in the same transaction
     */
    @Override
    @Transactional
    public DocumentUpdateResponse uploadOrReplace(String publicRef, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        final byte[] content;
        try {
            content = file.getBytes();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read file", e);
        }

        String contentType = (file.getContentType() == null || file.getContentType().isBlank())
                ? "application/octet-stream"
                : file.getContentType();

        String fileName = (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank())
                ? "document"
                : file.getOriginalFilename();

        // Find user id by publicRef (owner-protected repository method)
        UserIdView userIdView = writeRepository.findUserIdByPublicRef(publicRef)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UUID userId = userIdView.getId();

        // Get a reference without loading full user entity graph
        UserAccount userRef = em.getReference(UserAccount.class, userId);

        Optional<DocumentIdView> existingDocId = writeRepository.findDocumentIdByUserPublicRef(publicRef);

        IdentityDocument doc;
        if (existingDocId.isPresent()) {
            // Replace: load existing doc via EntityManager to update fields without exposing entity to controller
            UUID docId = existingDocId.get().getId();
            doc = em.getReference(IdentityDocument.class, docId);
        } else {
            // Create new
            doc = new IdentityDocument();
            doc.setUser(userRef);
        }

        doc.setFileName(fileName);
        doc.setContentType(contentType);
        doc.setContent(content);

        writeRepository.save(doc);

        auditEventRepository.save(new AuditEvent("IDENTITY_DOCUMENT_UPDATED", "AUTHENTICATED", userId));

        return new DocumentUpdateResponse("DOCUMENT_UPDATED");
    }
}
