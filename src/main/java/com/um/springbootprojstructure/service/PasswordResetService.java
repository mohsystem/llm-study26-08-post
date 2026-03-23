package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.ResetRequestRequest;
import com.um.springbootprojstructure.dto.ResetRequestResponse;

public interface PasswordResetService {
    ResetRequestResponse createResetRequest(ResetRequestRequest request);
}
