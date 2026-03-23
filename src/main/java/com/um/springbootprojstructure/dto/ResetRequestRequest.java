package com.um.springbootprojstructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetRequestRequest {

    /**
     * Email or username.
     */
    @NotBlank
    @Size(max = 200)
    private String account;

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
}
