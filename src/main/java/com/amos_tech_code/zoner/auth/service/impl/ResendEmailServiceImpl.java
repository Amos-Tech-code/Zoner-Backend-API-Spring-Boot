package com.amos_tech_code.zoner.auth.service.impl;

import com.amos_tech_code.zoner.auth.service.EmailService;
import com.amos_tech_code.zoner.config.properties.MailProperties;
import com.amos_tech_code.zoner.users.enums.AccountStatus;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("prod")
public class ResendEmailServiceImpl implements EmailService {

    private final Resend resend;
    private final MailProperties mailProperties;

    public ResendEmailServiceImpl(
            @Value("${resend.api-key}") String apiKey,
            MailProperties mailProperties
    ) {
        this.resend = new Resend(apiKey);
        this.mailProperties = mailProperties;
    }

    @Override
    public void sendVerificationCode(String email, String code) {

        send(
                email,
                "Verify your Zoner account",
                """
                <h2>Welcome to Zoner!</h2>

                <p>Your verification code is:</p>

                <h1>%s</h1>

                <p>This code expires in 10 minutes.</p>

                <p>If you didn't request this email,
                you can safely ignore it.</p>
                """.formatted(code)
        );
    }

    @Override
    public void sendEmailVerifiedNotification(String email) {

        send(
                email,
                "Your Zoner account has been verified",
                """
                <h2>Hello,</h2>

                <p>Your account has been verified successfully.</p>

                <p>If you made this change, no further action is required.</p>

                <p>If you did NOT verify your account,
                please contact support immediately.</p>

                <p>Regards,<br>
                Zoner Team</p>
                """
        );
    }

    @Override
    public void sendPasswordResetCode(String email, String code) {

        send(
                email,
                "Reset your Zoner password",
                """
                <h2>Welcome to Zoner!</h2>

                <p>Your password reset code is:</p>

                <h1>%s</h1>

                <p>This code expires in 10 minutes.</p>

                <p>If you didn't request this email,
                you can safely ignore it.</p>
                """.formatted(code)
        );
    }

    @Override
    public void sendPasswordChangedNotification(String email) {

        send(
                email,
                "Your Zoner password has been changed",
                """
                <h2>Hello,</h2>

                <p>Your password has been changed successfully.</p>

                <p>If you made this change, no further action is required.</p>

                <p>If you did NOT change your password,
                please contact support immediately.</p>

                <p>Regards,<br>
                Zoner Team</p>
                """
        );
    }

    @Override
    public void sendAccountStatusChangedNotification(
            String email,
            AccountStatus status
    ) {

        send(
                email,
                "Your Zoner account status has been changed",
                """
                <h2>Hello,</h2>

                <p>Your account status has been changed to:</p>

                <h3>%s</h3>

                <p>If you made this change, no further action is required.</p>

                <p>If you did NOT change your account status,
                please contact support immediately.</p>

                <p>Regards,<br>
                Zoner Team</p>
                """.formatted(status.name())
        );
    }

    private void send(
            String email,
            String subject,
            String html
    ) {

        try {

            CreateEmailOptions request = CreateEmailOptions.builder()
                    .from(mailProperties.getFrom())
                    .to(email)
                    .subject(subject)
                    .html(html)
                    .build();

            var response = resend.emails().send(request);

            log.info(
                    "Email sent successfully to {}. Resend ID: {}",
                    email,
                    response.getId()
            );

        } catch (ResendException ex) {

            log.error(
                    "Failed to send email to {} via Resend",
                    email,
                    ex
            );

            throw new RuntimeException(
                    "Failed to send email",
                    ex
            );
        }
    }
}