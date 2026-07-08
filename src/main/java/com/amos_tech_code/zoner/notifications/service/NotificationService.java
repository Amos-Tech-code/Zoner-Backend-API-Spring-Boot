package com.amos_tech_code.zoner.notifications.service;

import com.amos_tech_code.zoner.notifications.dto.PushNotificationRequest;

import java.util.UUID;

public interface NotificationService {

    void sendToUser(
            UUID userId,
            PushNotificationRequest request
    );

}
