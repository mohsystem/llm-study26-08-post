package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.ResetRequestRequest;
import com.um.springbootprojstructure.dto.ResetRequestResponse;
import com.um.springbootprojstructure.entity.AuditEvent;
import com.um.springbootprojstructure.entity.PasswordResetRequest;
import com.um.springbootprojstructure.entity.UserAccount;
import com.um.springbootprojstructure.repository.AuditEventRepository;
import com.um.springbootprojstructure.repository.PasswordResetLookupRepository;
import com.um.springbootprojstructure.repository.PasswordResetRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetLookupRepository lookupRepository;
    private final PasswordResetRequestRepository resetRequestRepository;
    private final AuditEventRepository auditEventRepository;

    private static final SecureRandom RNG = new SecureRandom();
    private static final Duration RESET_TTL = Duration.ofMinutes(30);

    public PasswordResetServiceImpl(PasswordResetLookupRepository lookupRepository,
                                    PasswordResetRequestRepository resetRequestRepository,
                                    AuditEventRepository auditEventRepository) {
        this.lookupRepository = lookupRepository;
        this.resetRequestRepository = resetRequestRepository;
        this.auditEventRepository = auditEventRepository;
    }

    /**
     * Security-sensitive write: must be transactional and must emit an audit event in same tx.
     * Prevents account enumeration: always returns same status.
     */
    @Override
    @Transactional
    public ResetRequestResponse createResetRequest(ResetRequestRequest request) {
        String account = request.getAccount() == null ? null : request.getAccount().trim();

        Optional<UserAccount> userOpt = lookupRepository.findActiveAccountForReset(account);

        if (userOpt.isEmpty()) {
            auditEventRepository.save(new AuditEvent("PASSWORD_RESET_REQUESTED_UNKNOWN_ACCOUNT", "ANONYMOUS", null));
            return new ResetRequestResponse("RESET_REQUEST_ACCEPTED");
        }

        UserAccount user = userOpt.get();

        // generate token (raw not returned by this endpoint per requirement)
        String rawToken = generateOpaqueToken();
        String tokenHash = sha256Hex(rawToken);

        PasswordResetRequest prr = new PasswordResetRequest();
        prr.setUser(user);
        prr.setTokenHash(tokenHash);
        prr.setExpiresAt(Instant.now().plus(RESET_TTL));
        prr.setUsed(false);

        resetRequestRepository.save(prr);

        auditEventRepository.save(new AuditEvent("PASSWORD_RESET_REQUESTED", "ANONYMOUS", user.getId()));

        // Do not expose whether account exists
        return new ResetRequestResponse("RESET_REQUEST_ACCEPTED");
    }

    private static String generateOpaqueToken() {
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
            throw new IllegalStateException("Unable to hash reset token", e);
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
