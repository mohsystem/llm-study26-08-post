package com.um.springbootprojstructure.repository.projection;

import com.um.springbootprojstructure.entity.MfaStatus;

import java.time.Instant;
import java.util.UUID;

public interface SessionForMfaView {
    UUID getId();
    UUID getUserId();
    String getPhoneNumber();
    MfaStatus getMfaStatus();
    Instant getExpiresAt();
    boolean isRevoked();
}
