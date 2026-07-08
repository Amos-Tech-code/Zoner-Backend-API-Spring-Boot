package com.amos_tech_code.zoner.notifications.service.impl;

import com.amos_tech_code.zoner.devices.entity.DeviceToken;
import com.amos_tech_code.zoner.devices.repository.DeviceTokenRepository;
import com.amos_tech_code.zoner.notifications.dto.PushNotificationRequest;
import com.amos_tech_code.zoner.notifications.service.NotificationService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FcmNotificationServiceImpl implements NotificationService {

    private final DeviceTokenRepository deviceTokenRepository;

    @Override
    public void sendToUser(
            UUID userId,
            PushNotificationRequest request
    ) {

        List<DeviceToken> devices =
                deviceTokenRepository.findAllByUserId(userId);

        for (DeviceToken device : devices) {

            Message message = Message.builder()
                    .setToken(device.getFcmToken())
                    .setNotification(
                            Notification.builder()
                                    .setTitle(request.title())
                                    .setBody(request.body())
                                    .build()
                    )
                    .putAllData(request.data())
                    .build();

            try {

                FirebaseMessaging.getInstance().send(message);

            } catch (FirebaseMessagingException e) {

                // Later we'll handle invalid tokens here

            }
        }
    }

}
