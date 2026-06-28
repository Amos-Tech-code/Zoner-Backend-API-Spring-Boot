package com.amos_tech_code.zoner.auth.service;

import com.amos_tech_code.zoner.auth.dto.request.LoginRequest;
import com.amos_tech_code.zoner.auth.dto.request.RegisterRequest;
import com.amos_tech_code.zoner.auth.dto.request.ResendVerificationRequest;
import com.amos_tech_code.zoner.auth.dto.request.VerifyEmailRequest;
import com.amos_tech_code.zoner.auth.dto.response.LoginResponse;
import com.amos_tech_code.zoner.auth.dto.response.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    void verifyEmail(VerifyEmailRequest request);

    void resendVerificationCode(
            ResendVerificationRequest request
    );

    LoginResponse login(LoginRequest request);

}
