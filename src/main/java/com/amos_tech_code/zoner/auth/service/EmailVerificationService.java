package com.amos_tech_code.zoner.auth.service;

import com.amos_tech_code.zoner.auth.dto.response.RegisterResponse;
import com.amos_tech_code.zoner.auth.entity.EmailVerification;
import com.amos_tech_code.zoner.users.entity.User;

public interface EmailVerificationService {

    void createVerification(User user);

    RegisterResponse resendVerification(User user);

    EmailVerification getActiveVerification(User user);

    void incrementAttempts(EmailVerification verification);

    void markVerified(EmailVerification verification);

}
