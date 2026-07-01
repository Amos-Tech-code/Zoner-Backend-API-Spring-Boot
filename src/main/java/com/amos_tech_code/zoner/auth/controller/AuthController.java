package com.amos_tech_code.zoner.auth.controller;

import com.amos_tech_code.zoner.auth.dto.request.CompleteProfileRequest;
import com.amos_tech_code.zoner.auth.dto.request.RegisterRequest;
import com.amos_tech_code.zoner.auth.dto.request.ResendVerificationRequest;
import com.amos_tech_code.zoner.auth.dto.request.VerifyEmailRequest;
import com.amos_tech_code.zoner.auth.dto.response.CompleteProfileResponse;
import com.amos_tech_code.zoner.auth.dto.response.RegisterResponse;
import com.amos_tech_code.zoner.auth.dto.response.VerifyEmailResponse;
import com.amos_tech_code.zoner.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return authService.register(request);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<VerifyEmailResponse> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request
    ) {

        VerifyEmailResponse response = authService.verifyEmail(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-verify-email")
    public ResponseEntity<RegisterResponse> resendVerifyEmail(
            @Valid @RequestBody ResendVerificationRequest request
    ) {
        RegisterResponse response = authService.resendVerificationCode(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/complete-profile")
    public ResponseEntity<CompleteProfileResponse> completeProfile(
            @Valid @RequestBody CompleteProfileRequest request
    ) {

        return ResponseEntity.ok(authService.completeProfile(request));

    }

}
