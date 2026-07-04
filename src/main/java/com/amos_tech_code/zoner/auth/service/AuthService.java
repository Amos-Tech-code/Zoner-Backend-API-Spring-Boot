package com.amos_tech_code.zoner.auth.service;

import com.amos_tech_code.zoner.auth.dto.request.*;
import com.amos_tech_code.zoner.auth.dto.response.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.UUID;

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

    MessageResponse logout(LogoutRequest request);

    MessageResponse logoutAll(UUID userId);

    List<SessionResponse> getSessions(
            UUID userId,
            UUID currentSessionId
    );

    MessageResponse revokeSession(
            UUID userId,
            UUID sessionId
    );
}
