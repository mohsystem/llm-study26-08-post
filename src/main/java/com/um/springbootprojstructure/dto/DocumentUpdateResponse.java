package com.um.springbootprojstructure.dto;

public class DocumentUpdateResponse {

    private String status;

    public DocumentUpdateResponse() {}

    public DocumentUpdateResponse(String status) {
        this.status = status;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
