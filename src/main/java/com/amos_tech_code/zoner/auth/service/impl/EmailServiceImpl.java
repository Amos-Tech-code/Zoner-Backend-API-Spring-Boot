package com.amos_tech_code.zoner.auth.service.impl;

import com.amos_tech_code.zoner.config.properties.MailProperties;
import com.amos_tech_code.zoner.auth.service.EmailService;
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

}
