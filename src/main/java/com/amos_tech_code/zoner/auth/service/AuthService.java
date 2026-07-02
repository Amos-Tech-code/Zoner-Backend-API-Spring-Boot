package com.amos_tech_code.zoner.auth.service;

import com.amos_tech_code.zoner.auth.dto.request.*;
import com.amos_tech_code.zoner.auth.dto.response.CompleteProfileResponse;
import com.amos_tech_code.zoner.auth.dto.response.LoginResponse;
import com.amos_tech_code.zoner.auth.dto.response.RegisterResponse;
import com.amos_tech_code.zoner.auth.dto.response.VerifyEmailResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    VerifyEmailResponse verifyEmail(VerifyEmailRequest request);

    RegisterResponse resendVerificationCode(
            ResendVerificationRequest request
    );

    CompleteProfileResponse completeProfile(CompleteProfileRequest request);

    LoginResponse login(
            LoginRequest request,
            HttpServletRequest httpRequest
    );

    LoginResponse refresh(
            RefreshTokenRequest request
    );
}
