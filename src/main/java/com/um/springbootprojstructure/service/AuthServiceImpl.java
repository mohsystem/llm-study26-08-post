package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.LoginRequest;
import com.um.springbootprojstructure.dto.LoginResponse;
import com.um.springbootprojstructure.dto.RegisterRequest;
import com.um.springbootprojstructure.dto.RegisterResponse;
import com.um.springbootprojstructure.service.exception.DuplicateAccountException;
import com.um.springbootprojstructure.service.exception.InvalidCredentialsException;
import com.um.springbootprojstructure.entity.AuditEvent;
import com.um.springbootprojstructure.entity.SessionToken;
import com.um.springbootprojstructure.entity.UserAccount;
import com.um.springbootprojstructure.repository.AuditEventRepository;
import com.um.springbootprojstructure.repository.LoginRepository;
import com.um.springbootprojstructure.repository.RegistrationRepository;
import com.um.springbootprojstructure.repository.SessionTokenRepository;
import com.um.springbootprojstructure.repository.UserAccountRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserAccountRepository userAccountRepository;
    private final RegistrationRepository registrationRepository;
    private final AuditEventRepository auditEventRepository;
    private final PasswordEncoder passwordEncoder;

    // login additions
    private final LoginRepository loginRepository;
    private final SessionTokenRepository sessionTokenRepository;

    private static final Duration SESSION_TTL = Duration.ofHours(12);
    private static final SecureRandom RNG = new SecureRandom();

    public AuthServiceImpl(
            UserAccountRepository userAccountRepository,
            RegistrationRepository registrationRepository,
            AuditEventRepository auditEventRepository,
            PasswordEncoder passwordEncoder,
            LoginRepository loginRepository,
            SessionTokenRepository sessionTokenRepository
    ) {
        this.userAccountRepository = userAccountRepository;
        this.registrationRepository = registrationRepository;
        this.auditEventRepository = auditEventRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginRepository = loginRepository;
        this.sessionTokenRepository = sessionTokenRepository;
    }

    /**
     * Security-sensitive write: must be transactional and must emit an audit event in same tx.
     */
    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // Prevent account enumeration nuances: we still validate uniqueness but keep generic errors if desired.
        if (registrationRepository.usernameExists(request.getUsername())) {
            throw new DuplicateAccountException();
        }
        if (registrationRepository.emailExists(request.getEmail())) {
            throw new DuplicateAccountException();
        }

        UserAccount u = new UserAccount();
        u.setUsername(request.getUsername().trim());
        u.setEmail(request.getEmail().trim());
        u.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        u.setActive(true);

        UserAccount saved = userAccountRepository.save(u);

        // Audit within same transaction
        auditEventRepository.save(new AuditEvent(
                "USER_REGISTERED",
                "ANONYMOUS",
                saved.getId()
        ));

        return new RegisterResponse(saved.getId(), "REGISTERED");
    }

    /**
     * Security-sensitive write: must be transactional and must emit an audit event in same tx.
     */
    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        String login = request.getLogin() == null ? null : request.getLogin().trim();

        UserAccount user = loginRepository.findForLogin(login)
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.isActive()) {
            auditEventRepository.save(new AuditEvent("USER_LOGIN_BLOCKED_INACTIVE", "ANONYMOUS", user.getId()));
            throw new InvalidCredentialsException();
        }

        boolean ok = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
        if (!ok) {
            auditEventRepository.save(new AuditEvent("USER_LOGIN_FAILED", "ANONYMOUS", user.getId()));
            throw new InvalidCredentialsException();
        }

        // Create opaque session token (return raw token; store only hash)
        String rawToken = generateOpaqueToken();
        String tokenHash = sha256Hex(rawToken);

        SessionToken st = new SessionToken();
        st.setUser(user);
        st.setTokenHash(tokenHash);
        st.setExpiresAt(Instant.now().plus(SESSION_TTL));
        st.setRevoked(false);

        sessionTokenRepository.save(st);

        auditEventRepository.save(new AuditEvent("USER_LOGIN_SUCCESS", "ANONYMOUS", user.getId()));

        return new LoginResponse(rawToken);
    }

    private static String generateOpaqueToken() {
        // 32 bytes -> 64 hex chars
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
            throw new IllegalStateException("Unable to hash token", e);
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
