package com.um.springbootprojstructure.dto;

public class ResetRequestResponse {

    private String status;

    public ResetRequestResponse() {}

    public ResetRequestResponse(String status) {
        this.status = status;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
