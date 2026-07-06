package com.amos_tech_code.zoner.auth.listener;

import com.amos_tech_code.zoner.auth.event.UserRegisteredEvent;
import com.amos_tech_code.zoner.auth.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegisteredEventListener {

    private final EmailService emailService;

    @Async
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(UserRegisteredEvent event) {

        log.info(
                "Sending verification email to {}",
                event.email()
        );

        emailService.sendVerificationCode(
                event.email(),
                event.otp()
        );
    }

}
