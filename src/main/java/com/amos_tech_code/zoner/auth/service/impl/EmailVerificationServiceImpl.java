package com.amos_tech_code.zoner.auth.service.impl;

import com.amos_tech_code.zoner.users.entity.User;
import org.springframework.stereotype.Service;
import com.amos_tech_code.zoner.auth.service.EmailVerificationService;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

    @Override
    public void createVerification(User user) {

    }

    @Override
    public void verifyCode(User user, String code) {

    }

    @Override
    public void resend(User user) {

    }
}
