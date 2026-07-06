package com.amos_tech_code.zoner.auth.service;

public interface EmailService {

    void sendVerificationCode(
            String email,
            String code
    );

    void sendPasswordResetCode(
            String email,
            String code
    );

    void sendPasswordChangedNotification(
            String email
    );


}
