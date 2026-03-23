package com.um.springbootprojstructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    /**
     * Accepts either username or email.
     */
    @NotBlank
    @Size(max = 200)
    private String login;

    @NotBlank
    @Size(min = 8, max = 72)
    private String password;

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
