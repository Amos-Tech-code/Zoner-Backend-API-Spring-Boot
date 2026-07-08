package com.amos_tech_code.zoner.auth.listener;

import com.amos_tech_code.zoner.auth.event.AccountStatusChangedEvent;
import com.amos_tech_code.zoner.auth.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountStatusChangedEventListener {

    private final EmailService emailService;

    @Async
    @EventListener
    public void handleAccountStatusChanged(
            AccountStatusChangedEvent event
    ) {

        log.info(
                "Sending account status change notification to {}",
                event.email()
        );

        emailService.sendAccountStatusChangedNotification(
                event.email(),
                event.status()
        );

    }

}
