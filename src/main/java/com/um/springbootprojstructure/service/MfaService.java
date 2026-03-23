package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.MfaChallengeRequest;
import com.um.springbootprojstructure.dto.MfaChallengeResponse;
import com.um.springbootprojstructure.dto.MfaVerifyRequest;
import com.um.springbootprojstructure.dto.MfaVerifyResponse;

public interface MfaService {
    MfaChallengeResponse challenge(MfaChallengeRequest request);
    MfaVerifyResponse verify(MfaVerifyRequest request);
}
