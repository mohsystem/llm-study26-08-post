package com.um.springbootprojstructure.repository.projection;

import com.um.springbootprojstructure.entity.ApiKeyStatus;

import java.time.Instant;
import java.util.UUID;

public interface ApiKeyListView {
    UUID getId();
    String getName();
    ApiKeyStatus getStatus();
    Instant getCreatedAt();
    Instant getRevokedAt();
}
