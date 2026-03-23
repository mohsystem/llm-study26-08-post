package com.um.springbootprojstructure.dto;

import java.util.UUID;

public class ApiKeyRevokeResponse {
    private UUID keyId;
    private String status;

    public ApiKeyRevokeResponse() {}

    public ApiKeyRevokeResponse(UUID keyId, String status) {
        this.keyId = keyId;
        this.status = status;
    }

    public UUID getKeyId() { return keyId; }
    public void setKeyId(UUID keyId) { this.keyId = keyId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
