package com.um.springbootprojstructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetConfirmRequest {

    @NotBlank
    @Size(min = 32, max = 256)
    private String resetToken;

    @NotBlank
    @Size(min = 8, max = 72)
    private String newPassword;

    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
