package com.um.springbootprojstructure.dto;

public class OperationResultResponse {

    /**
     * Deterministic status string. Examples:
     * ACCEPTED, REJECTED, AUTHENTICATED, INVALID_CREDENTIALS, PASSWORD_CHANGED, RESET_REQUEST_ACCEPTED, PASSWORD_RESET
     */
    private String status;

    /**
     * Optional deterministic reason code (never include PII or internal exception text).
     * Examples: DUPLICATE_ACCOUNT, INVALID_INPUT, INVALID_TOKEN, POLICY_VIOLATION
     */
    private String reason;

    public OperationResultResponse() {}

    public OperationResultResponse(String status) {
        this.status = status;
    }

    public OperationResultResponse(String status, String reason) {
        this.status = status;
        this.reason = reason;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
