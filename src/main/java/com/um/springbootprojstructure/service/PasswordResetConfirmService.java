package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.ResetConfirmRequest;
import com.um.springbootprojstructure.dto.ResetConfirmResponse;

public interface PasswordResetConfirmService {
    ResetConfirmResponse confirm(ResetConfirmRequest request);
}
