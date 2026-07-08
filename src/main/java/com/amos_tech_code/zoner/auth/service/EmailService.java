package com.amos_tech_code.zoner.auth.service;

import com.amos_tech_code.zoner.users.enums.AccountStatus;

public interface EmailService {

    void sendVerificationCode(
            String email,
            String code
    );

    void sendEmailVerifiedNotification(
            String email
    );

    void sendPasswordResetCode(
            String email,
            String code
    );

    void sendPasswordChangedNotification(
            String email
    );

    void sendAccountStatusChangedNotification(
            String email,
            AccountStatus status
    );


}
