package com.amos_tech_code.zoner.notifications.dto;

import java.util.Map;

public record PushNotificationRequest(

        String title,

        String body,

        Map<String, String> data

) {}
