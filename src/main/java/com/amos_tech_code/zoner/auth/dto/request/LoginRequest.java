package com.amos_tech_code.zoner.auth.dto.request;

import com.amos_tech_code.zoner.common.enums.DevicePlatform;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @Email(message = "Invalid email format.")
        @NotBlank(message = "Email is required.")
        String email,

        @NotBlank(message = "Password is required.")
        String password,

        @NotBlank(message = "Device ID is required.")
        String deviceId,

        String deviceName,

        DevicePlatform platform

) {}