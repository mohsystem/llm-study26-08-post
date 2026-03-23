package com.um.springbootprojstructure.controller;

import com.um.springbootprojstructure.dto.*;
import com.um.springbootprojstructure.service.AuthService;
import com.um.springbootprojstructure.service.PasswordService;
import com.um.springbootprojstructure.service.PasswordResetService;
import com.um.springbootprojstructure.service.PasswordResetConfirmService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordService passwordService;
    private final PasswordResetService passwordResetService;
    private final PasswordResetConfirmService passwordResetConfirmService;

    public AuthController(AuthService authService,
                          PasswordService passwordService,
                          PasswordResetService passwordResetService,
                          PasswordResetConfirmService passwordResetConfirmService) {
        this.authService = authService;
        this.passwordService = passwordService;
        this.passwordResetService = passwordResetService;
        this.passwordResetConfirmService = passwordResetConfirmService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/change-password")
    @ResponseStatus(HttpStatus.OK)
    public StatusResponse changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        return passwordService.changePassword(request);
    }

    @PostMapping("/reset-request")
    @ResponseStatus(HttpStatus.OK)
    public ResetRequestResponse resetRequest(@Valid @RequestBody ResetRequestRequest request) {
        return passwordResetService.createResetRequest(request);
    }

    @PostMapping("/reset-confirm")
    @ResponseStatus(HttpStatus.OK)
    public ResetConfirmResponse resetConfirm(@Valid @RequestBody ResetConfirmRequest request) {
        return passwordResetConfirmService.confirm(request);
    }
}
