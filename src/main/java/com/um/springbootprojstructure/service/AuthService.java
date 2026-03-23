package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.RegisterRequest;
import com.um.springbootprojstructure.dto.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}
