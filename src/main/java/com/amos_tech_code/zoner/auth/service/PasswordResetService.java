package com.amos_tech_code.zoner.auth.service;

import com.amos_tech_code.zoner.auth.entity.PasswordResetToken;
import com.amos_tech_code.zoner.users.entity.User;

public interface PasswordResetService {

    void create(User user);

    PasswordResetToken getActive(User user);

    void resend(User user);

    void incrementAttempts(PasswordResetToken token);

    void markVerified(PasswordResetToken token);

}
