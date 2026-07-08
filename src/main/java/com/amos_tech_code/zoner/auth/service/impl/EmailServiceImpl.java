package com.amos_tech_code.zoner.auth.service.impl;

import com.amos_tech_code.zoner.config.properties.MailProperties;
import com.amos_tech_code.zoner.auth.service.EmailService;
import com.amos_tech_code.zoner.users.enums.AccountStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    private final MailProperties mailProperties;

    @Override
    public void sendVerificationCode(String email, String code) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);

        message.setFrom(mailProperties.getFrom());

        message.setSubject("Verify your Zoner account");

        message.setText("""
                Welcome to Zoner!

                Your verification code is:

                %s

                This code expires in 10 minutes.

                If you didn't request this email,
                you can safely ignore it.
                """.formatted(code));

        try {

            mailSender.send(message);

        }
        catch (MailException ex) {

            log.error(
                    "Failed to send verification email to {}",
                    email,
                    ex
            );

            throw ex;

        }

    }

    @Override
    public void sendEmailVerifiedNotification(String email) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(mailProperties.getFrom());
        message.setSubject("Your Zoner account has been verified");
        message.setText(
                """
                Hello,
    
                Your account has been verified successfully.
                
                If you made this change, no further action is required.
    
                If you did NOT verify your account, please contact support immediately.
    
                Regards,
                Zoner Team
                """
        );
    }

    @Override
    public void sendPasswordResetCode(String email, String code) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);

        message.setFrom(mailProperties.getFrom());

        message.setSubject("Reset your Zoner password");

        message.setText("""
                Welcome to Zoner!
                
                Your password reset code is:
                
                %s
                
                This code expires in 10 minutes.
                
                If you didn't request this email,
                you can safely ignore it.
                """.formatted(code));

        try {

            mailSender.send(message);

        }
        catch (MailException ex) {

            log.error(
                    "Failed to send verification email to {}",
                    email,
                    ex
            );

            throw ex;

        }

    }

    @Override
    public void sendPasswordChangedNotification(String email) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(mailProperties.getFrom());
        message.setSubject("Your Zoner password has been changed");
        message.setText("""
                Hello,
    
                Your password has been changed successfully.
    
                If you made this change, no further action is required.
    
                If you did NOT change your password, please contact support immediately.
    
                Regards,
                Zoner Team
                """);
        try {
            mailSender.send(message);
        } catch (MailException ex) {
            log.error(
                    "Failed to send verification email to {}",
                    email,
                    ex
            );
            throw ex;
        }

    }

    @Override
    public void sendAccountStatusChangedNotification(
            String email,
            AccountStatus status
    ) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(mailProperties.getFrom());
        message.setSubject("Your Zoner account status has been changed");
        message.setText("""
                Hello,
    
                Your account status has been changed to %s.
                
                If you made this change, no further action is required.
                
                If you did NOT change your account status, please contact support immediately.
    
                Regards,
                Zoner Team
                )""".formatted(status.name()));
        try {
            mailSender.send(message);

        } catch (MailException ex) {
            log.error(
                    "Failed to send verification email to {}",
                    email,
                    ex
            );
            throw ex;
        }
    }


}
