package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.ChangePasswordRequest;
import com.um.springbootprojstructure.dto.StatusResponse;

public interface PasswordService {
    StatusResponse changePassword(ChangePasswordRequest request);
}
