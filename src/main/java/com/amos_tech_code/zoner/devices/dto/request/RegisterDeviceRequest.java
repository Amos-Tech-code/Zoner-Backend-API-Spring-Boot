package com.amos_tech_code.zoner.devices.dto.request;

import com.amos_tech_code.zoner.common.enums.DevicePlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterDeviceRequest(

        @NotBlank(message = "Device ID is required.")
        String deviceId,

        @NotBlank(message = "FCM token is required.")
        String fcmToken,

        @NotNull(message = "Platform is required.")
        DevicePlatform platform,

        String deviceName,

        String deviceModel,

        String appVersion

) {
}