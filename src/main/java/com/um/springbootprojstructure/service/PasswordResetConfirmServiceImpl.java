package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.ResetConfirmRequest;
import com.um.springbootprojstructure.dto.ResetConfirmResponse;
import com.um.springbootprojstructure.service.exception.InvalidTokenException;
import com.um.springbootprojstructure.entity.AuditEvent;
import com.um.springbootprojstructure.entity.PasswordResetRequest;
import com.um.springbootprojstructure.repository.AuditEventRepository;
import com.um.springbootprojstructure.repository.PasswordResetConfirmRepository;
import com.um.springbootprojstructure.repository.UserAccountPasswordUpdateRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;

@Service
public class PasswordResetConfirmServiceImpl implements PasswordResetConfirmService {

    private final PasswordResetConfirmRepository confirmRepository;
    private final UserAccountPasswordUpdateRepository passwordUpdateRepository;
    private final AuditEventRepository auditEventRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetConfirmServiceImpl(PasswordResetConfirmRepository confirmRepository,
                                          UserAccountPasswordUpdateRepository passwordUpdateRepository,
                                          AuditEventRepository auditEventRepository,
                                          PasswordEncoder passwordEncoder) {
        this.confirmRepository = confirmRepository;
        this.passwordUpdateRepository = passwordUpdateRepository;
        this.auditEventRepository = auditEventRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Security-sensitive write:
     * - @Transactional
     * - audit event in same transaction
     * - token replay prevention (mark used)
     */
    @Override
    @Transactional
    public ResetConfirmResponse confirm(ResetConfirmRequest request) {
        String token = request.getResetToken() == null ? null : request.getResetToken().trim();
        String tokenHash = sha256Hex(token);

        PasswordResetRequest rr = confirmRepository.findValidByTokenHash(tokenHash, Instant.now())
                .orElseThrow(InvalidTokenException::new);

        String newHash = passwordEncoder.encode(request.getNewPassword());

        int updated = passwordUpdateRepository.updatePasswordHashByUserId(rr.getUser().getId(), newHash);
        if (updated != 1) {
            auditEventRepository.save(new AuditEvent("PASSWORD_RESET_CONFIRM_FAILED", "ANONYMOUS", rr.getUser().getId()));
            throw new IllegalStateException("Password update failed");
        }

        int marked = confirmRepository.markUsed(rr.getId());
        if (marked != 1) {
            auditEventRepository.save(new AuditEvent("PASSWORD_RESET_CONFIRM_FAILED", "ANONYMOUS", rr.getUser().getId()));
            throw new IllegalStateException("Reset token finalization failed");
        }

        auditEventRepository.save(new AuditEvent("PASSWORD_RESET_CONFIRMED", "ANONYMOUS", rr.getUser().getId()));
        return new ResetConfirmResponse("PASSWORD_RESET");
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
