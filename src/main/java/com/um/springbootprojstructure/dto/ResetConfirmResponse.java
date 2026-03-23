package com.um.springbootprojstructure.dto;

public class ResetConfirmResponse {

    private String status;

    public ResetConfirmResponse() {}

    public ResetConfirmResponse(String status) {
        this.status = status;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
