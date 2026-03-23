package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.ChangePasswordRequest;
import com.um.springbootprojstructure.dto.StatusResponse;
import com.um.springbootprojstructure.service.exception.InvalidCredentialsException;
import com.um.springbootprojstructure.entity.AuditEvent;
import com.um.springbootprojstructure.entity.UserAccount;
import com.um.springbootprojstructure.repository.AuditEventRepository;
import com.um.springbootprojstructure.repository.UserAccountPasswordRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordServiceImpl implements PasswordService {

    private final UserAccountPasswordRepository passwordRepository;
    private final AuditEventRepository auditEventRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordServiceImpl(UserAccountPasswordRepository passwordRepository,
                               AuditEventRepository auditEventRepository,
                               PasswordEncoder passwordEncoder) {
        this.passwordRepository = passwordRepository;
        this.auditEventRepository = auditEventRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Security-sensitive write:
     * - @Transactional
     * - audit event within the same transaction
     */
    @Override
    @Transactional
    public StatusResponse changePassword(ChangePasswordRequest request) {
        UserAccount current = passwordRepository.findCurrentUserForPasswordChange()
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean matches = passwordEncoder.matches(request.getCurrentPassword(), current.getPasswordHash());
        if (!matches) {
            // audit within same transaction
            auditEventRepository.save(new AuditEvent("USER_PASSWORD_CHANGE_FAILED", "AUTHENTICATED", current.getId()));
            throw new InvalidCredentialsException();
        }

        String newHash = passwordEncoder.encode(request.getNewPassword());
        int updated = passwordRepository.updatePasswordHashForCurrentUser(newHash);
        if (updated != 1) {
            auditEventRepository.save(new AuditEvent("USER_PASSWORD_CHANGE_FAILED", "AUTHENTICATED", current.getId()));
            throw new IllegalStateException("Password update failed");
        }

        auditEventRepository.save(new AuditEvent("USER_PASSWORD_CHANGED", "AUTHENTICATED", current.getId()));
        return new StatusResponse("PASSWORD_CHANGED");
    }
}
