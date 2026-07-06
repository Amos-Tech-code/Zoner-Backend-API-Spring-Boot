package com.amos_tech_code.zoner.auth.listener;

import com.amos_tech_code.zoner.auth.event.PasswordChangedEvent;
import com.amos_tech_code.zoner.auth.event.PasswordResetRequestedEvent;
import com.amos_tech_code.zoner.auth.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordResetEventListener {

    private final EmailService emailService;

    @Async
    @EventListener
    public void handlePasswordResetRequested(
            PasswordResetRequestedEvent event
    ) {

        log.info(
                "Sending password reset code to {}",
                event.email()
        );

        emailService.sendPasswordResetCode(
                event.email(),
                event.code()
        );

    }

    @Async
    @EventListener
    public void handlePasswordChanged(
            PasswordChangedEvent event
    ) {

        log.info(
                "Sending password changed notification to {}",
                event.email()
        );

        emailService.sendPasswordChangedNotification(
                event.email()
        );

    }

}