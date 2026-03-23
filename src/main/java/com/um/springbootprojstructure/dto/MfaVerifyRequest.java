package com.um.springbootprojstructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class MfaVerifyRequest {

    @NotBlank
    @Size(min = 32, max = 256)
    private String sessionToken;

    @NotBlank
    @Pattern(regexp = "^[0-9]{6}$")
    private String passcode;

    public String getSessionToken() { return sessionToken; }
    public void setSessionToken(String sessionToken) { this.sessionToken = sessionToken; }

    public String getPasscode() { return passcode; }
    public void setPasscode(String passcode) { this.passcode = passcode; }
}
