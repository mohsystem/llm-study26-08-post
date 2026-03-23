package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.ApiKeyIssueRequest;
import com.um.springbootprojstructure.dto.ApiKeyIssueResponse;
import com.um.springbootprojstructure.dto.ApiKeyRevokeResponse;
import com.um.springbootprojstructure.entity.ApiKey;
import com.um.springbootprojstructure.entity.ApiKeyStatus;
import com.um.springbootprojstructure.entity.AuditEvent;
import com.um.springbootprojstructure.entity.UserAccount;
import com.um.springbootprojstructure.repository.ApiKeyRepository;
import com.um.springbootprojstructure.repository.AuditEventRepository;
import com.um.springbootprojstructure.repository.projection.ApiKeyListView;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;

@Service
public class ApiKeyServiceImpl implements ApiKeyService {

    private static final SecureRandom RNG = new SecureRandom();

    private final ApiKeyRepository apiKeyRepository;
    private final AuditEventRepository auditEventRepository;
    private final EntityManager em;

    public ApiKeyServiceImpl(ApiKeyRepository apiKeyRepository,
                             AuditEventRepository auditEventRepository,
                             EntityManager em) {
        this.apiKeyRepository = apiKeyRepository;
        this.auditEventRepository = auditEventRepository;
        this.em = em;
    }

    @Override
    @Transactional
    public ApiKeyIssueResponse issue(ApiKeyIssueRequest request) {
        UUID ownerId = currentPrincipalId();

        // generate secret and store only hash
        String rawKey = generateApiKey();
        String keyHash = sha256Hex(rawKey);

        ApiKey k = new ApiKey();
        k.setName(request.getName().trim());
        k.setKeyHash(keyHash);
        k.setStatus(ApiKeyStatus.ACTIVE);

        // owner reference without loading full entity
        UserAccount ownerRef = em.getReference(UserAccount.class, ownerId);
        k.setOwner(ownerRef);

        ApiKey saved = apiKeyRepository.save(k);

        auditEventRepository.save(new AuditEvent("API_KEY_ISSUED", "AUTHENTICATED", ownerId));

        // return secret only once
        return new ApiKeyIssueResponse(saved.getId(), "ISSUED", rawKey);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApiKeyListView> list(Pageable pageable) {
        // enforce pagination; callers provide Pageable, do not return unbounded lists
        return apiKeyRepository.listForCurrentOwner(pageable);
    }

    @Override
    @Transactional
    public ApiKeyRevokeResponse revoke(UUID keyId) {
        UUID ownerId = currentPrincipalId();

        int updated = apiKeyRepository.revokeOwned(keyId, ApiKeyStatus.REVOKED, Instant.now());
        if (updated != 1) {
            auditEventRepository.save(new AuditEvent("API_KEY_REVOKE_FAILED_NOT_FOUND", "AUTHENTICATED", ownerId));
            throw new IllegalArgumentException("API key not found");
        }

        auditEventRepository.save(new AuditEvent("API_KEY_REVOKED", "AUTHENTICATED", ownerId));
        return new ApiKeyRevokeResponse(keyId, "REVOKED");
    }

    /**
     * Minimal helper: expects Authentication principal to expose UUID id.
     * Your existing code already relies on principal.id in repository SpEL.
     */
    private static UUID currentPrincipalId() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || a.getPrincipal() == null) {
            throw new IllegalStateException("No authentication");
        }
        // Expect principal to have getId():UUID; if not, adjust to your principal type.
        try {
            Object principal = a.getPrincipal();
            return (UUID) principal.getClass().getMethod("getId").invoke(principal);
        } catch (Exception e) {
            throw new IllegalStateException("Principal does not expose UUID getId()", e);
        }
    }

    private static String generateApiKey() {
        // 32 bytes -> 64 hex chars (opaque)
        byte[] b = new byte[32];
        RNG.nextBytes(b);
        return toHex(b);
    }

    private static String sha256Hex(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            return toHex(dig);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to hash api key", e);
        }
    }

    private static String toHex(byte[] bytes) {
        char[] hex = new char[bytes.length * 2];
        final char[] digits = "0123456789abcdef".toCharArray();
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hex[i * 2] = digits[v >>> 4];
            hex[i * 2 + 1] = digits[v & 0x0F];
        }
        return new String(hex);
    }
}
