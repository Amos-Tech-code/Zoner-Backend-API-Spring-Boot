package com.amos_tech_code.zoner.auth.service;

import com.amos_tech_code.zoner.users.entity.User;

public interface EmailVerificationService {

    void createVerification(User user);

    void verifyCode(
            User user,
            String code
    );

    void resend(User user);

}
