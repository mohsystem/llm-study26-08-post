package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.MfaChallengeRequest;
import com.um.springbootprojstructure.dto.MfaChallengeResponse;
import com.um.springbootprojstructure.dto.MfaVerifyRequest;
import com.um.springbootprojstructure.dto.MfaVerifyResponse;
import com.um.springbootprojstructure.entity.AuditEvent;
import com.um.springbootprojstructure.entity.MfaChallenge;
import com.um.springbootprojstructure.entity.MfaStatus;
import com.um.springbootprojstructure.entity.SessionToken;
import com.um.springbootprojstructure.repository.AuditEventRepository;
import com.um.springbootprojstructure.repository.MfaChallengeRepository;
import com.um.springbootprojstructure.repository.MfaSessionRepository;
import com.um.springbootprojstructure.repository.projection.SessionForMfaView;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

@Service
public class MfaServiceImpl implements MfaService {

    private static final SecureRandom RNG = new SecureRandom();
    private static final Duration OTP_TTL = Duration.ofMinutes(5);
    private static final int MAX_ATTEMPTS = 5;

    private final MfaSessionRepository sessionRepository;
    private final MfaChallengeRepository challengeRepository;
    private final AuditEventRepository auditEventRepository;
    private final NotificationGatewayClient notificationGatewayClient;
    private final EntityManager em;

    public MfaServiceImpl(MfaSessionRepository sessionRepository,
                          MfaChallengeRepository challengeRepository,
                          AuditEventRepository auditEventRepository,
                          NotificationGatewayClient notificationGatewayClient,
                          EntityManager em) {
        this.sessionRepository = sessionRepository;
        this.challengeRepository = challengeRepository;
        this.auditEventRepository = auditEventRepository;
        this.notificationGatewayClient = notificationGatewayClient;
        this.em = em;
    }

    @Override
    @Transactional
    public MfaChallengeResponse challenge(MfaChallengeRequest request) {
        String tokenHash = sha256Hex(request.getSessionToken().trim());
        SessionForMfaView session = sessionRepository.findSessionForMfaByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid session"));

        Instant now = Instant.now();
        if (session.isRevoked() || session.getExpiresAt().isBefore(now)) {
            auditEventRepository.save(new AuditEvent("MFA_CHALLENGE_REJECTED_SESSION_INVALID", "ANONYMOUS", session.getUserId()));
            throw new IllegalArgumentException("Invalid session");
        }

        if (session.getMfaStatus() == MfaStatus.VERIFIED) {
            return new MfaChallengeResponse("ALREADY_VERIFIED");
        }

        if (session.getPhoneNumber() == null || session.getPhoneNumber().isBlank()) {
            auditEventRepository.save(new AuditEvent("MFA_CHALLENGE_FAILED_NO_DESTINATION", "ANONYMOUS", session.getUserId()));
            throw new IllegalStateException("MFA destination not configured");
        }

        String otp = generate6DigitOtp();
        String otpHash = sha256Hex(otp);

        MfaChallenge c = new MfaChallenge();
        SessionToken sessionRef = em.getReference(SessionToken.class, session.getId());
        c.setSession(sessionRef);
        c.setOtpHash(otpHash);
        c.setExpiresAt(now.plus(OTP_TTL));
        c.setAttempts(0);
        c.setVerified(false);

        challengeRepository.save(c);

        notificationGatewayClient.sendOtp(session.getPhoneNumber(), "Your one-time passcode is: " + otp);

        auditEventRepository.save(new AuditEvent("MFA_CHALLENGE_CREATED", "ANONYMOUS", session.getUserId()));
        return new MfaChallengeResponse("CHALLENGE_SENT");
    }

    @Override
    @Transactional
    public MfaVerifyResponse verify(MfaVerifyRequest request) {
        String tokenHash = sha256Hex(request.getSessionToken().trim());
        SessionForMfaView session = sessionRepository.findSessionForMfaByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid session"));

        Instant now = Instant.now();
        if (session.isRevoked() || session.getExpiresAt().isBefore(now)) {
            auditEventRepository.save(new AuditEvent("MFA_VERIFY_REJECTED_SESSION_INVALID", "ANONYMOUS", session.getUserId()));
            throw new IllegalArgumentException("Invalid session");
        }

        if (session.getMfaStatus() == MfaStatus.VERIFIED) {
            return new MfaVerifyResponse("AUTHENTICATED");
        }

        MfaChallenge challenge = challengeRepository.findLatestActiveBySession(session.getId(), now)
                .orElseThrow(() -> new IllegalArgumentException("No active challenge"));

        if (challenge.getAttempts() >= MAX_ATTEMPTS) {
            auditEventRepository.save(new AuditEvent("MFA_VERIFY_REJECTED_TOO_MANY_ATTEMPTS", "ANONYMOUS", session.getUserId()));
            throw new IllegalArgumentException("Invalid passcode");
        }

        // increment attempts regardless of outcome
        challengeRepository.incrementAttempts(challenge.getId());

        String submittedHash = sha256Hex(request.getPasscode());
        if (!submittedHash.equals(challenge.getOtpHash())) {
            auditEventRepository.save(new AuditEvent("MFA_VERIFY_FAILED", "ANONYMOUS", session.getUserId()));
            throw new IllegalArgumentException("Invalid passcode");
        }

        challengeRepository.markVerified(challenge.getId());
        sessionRepository.updateMfaStatus(session.getId(), MfaStatus.VERIFIED, now);

        auditEventRepository.save(new AuditEvent("MFA_VERIFIED", "ANONYMOUS", session.getUserId()));
        return new MfaVerifyResponse("AUTHENTICATED");
    }

    private static String generate6DigitOtp() {
        int v = RNG.nextInt(1_000_000);
        return String.format("%06d", v);
    }

    private static String sha256Hex(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            return toHex(dig);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to hash value", e);
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
