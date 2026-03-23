package com.um.springbootprojstructure.dto;

import java.util.UUID;

public class ApiKeyIssueResponse {

    private UUID keyId;
    private String status;

    /**
     * Returned only once upon issuance.
     */
    private String apiKey;

    public ApiKeyIssueResponse() {}

    public ApiKeyIssueResponse(UUID keyId, String status, String apiKey) {
        this.keyId = keyId;
        this.status = status;
        this.apiKey = apiKey;
    }

    public UUID getKeyId() { return keyId; }
    public void setKeyId(UUID keyId) { this.keyId = keyId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
}
