package com.um.springbootprojstructure.dto;

import java.util.UUID;

public class RegisterResponse {
    private UUID accountId;
    private String status;

    public RegisterResponse() {}

    public RegisterResponse(UUID accountId, String status) {
        this.accountId = accountId;
        this.status = status;
    }

    public UUID getAccountId() { return accountId; }
    public void setAccountId(UUID accountId) { this.accountId = accountId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
