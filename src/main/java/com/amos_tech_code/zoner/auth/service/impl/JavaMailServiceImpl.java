package com.amos_tech_code.zoner.auth.service.impl;

import com.amos_tech_code.zoner.config.properties.MailProperties;
import com.amos_tech_code.zoner.auth.service.EmailService;
import com.amos_tech_code.zoner.users.enums.AccountStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("dev")
@RequiredArgsConstructor
public class JavaMailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    @Override
    public void sendVerificationCode(String email, String code) {
        String subject = "Verify your Zoner account";
        String text = """
                Welcome to Zoner!

                Your verification code is:

                %s

                This code expires in 10 minutes.

                If you didn't request this email,
                you can safely ignore it.
                """.formatted(code);

        send(email, subject, text);
    }

    @Override
    public void sendEmailVerifiedNotification(String email) {
        String subject = "Your Zoner account has been verified";
        String text = """
                Hello,

                Your account has been verified successfully.

                If you made this change, no further action is required.

                If you did NOT verify your account, please contact support immediately.

                Regards,
                Zoner Team
                """;

        send(email, subject, text);
    }

    @Override
    public void sendPasswordResetCode(String email, String code) {
        String subject = "Reset your Zoner password";
        String text = """
                Welcome to Zoner!

                Your password reset code is:

                %s

                This code expires in 10 minutes.

                If you didn't request this email,
                you can safely ignore it.
                """.formatted(code);

        send(email, subject, text);
    }

    @Override
    public void sendPasswordChangedNotification(String email) {
        String subject = "Your Zoner password has been changed";
        String text = """
                Hello,

                Your password has been changed successfully.

                If you made this change, no further action is required.

                If you did NOT change your password, please contact support immediately.

                Regards,
                Zoner Team
                """;

        send(email, subject, text);
    }

    @Override
    public void sendAccountStatusChangedNotification(String email, AccountStatus status) {
        String subject = "Your Zoner account status has been changed";
        String text = """
                Hello,

                Your account status has been changed to %s.

                If you made this change, no further action is required.

                If you did NOT change your account status, please contact support immediately.

                Regards,
                Zoner Team
                """.formatted(status.name());

        send(email, subject, text);
    }

    /**
     * Centralized send method for plaintext emails.
     * Logs success/failure and rethrows MailException for callers to handle (same behavior as before).
     */
    private void send(String email, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(mailProperties.getFrom());
        message.setSubject(subject);
        message.setText(text);

        try {
            mailSender.send(message);
            log.info("Email sent successfully to {} (dev mail sender)", email);
        } catch (MailException ex) {
            log.error("Failed to send email to {}", email, ex);
            throw ex;
        }
    }
}