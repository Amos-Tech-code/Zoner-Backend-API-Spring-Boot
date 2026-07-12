package com.amos_tech_code.zoner.auth.dto.request;

import com.amos_tech_code.zoner.common.enums.DevicePlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GoogleLoginRequest(

        @NotBlank(message = "idToken is required")
        String idToken,

        @NotBlank(message = "deviceId is required")
        String deviceId,

        @NotBlank(message = "deviceName is required")
        String deviceName,

        @NotNull(message = "platform is required")
        DevicePlatform platform

) {}
