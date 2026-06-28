package com.amos_tech_code.zoner.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "zoner.auth")
@Data
public class AuthProperties {

    private Duration verificationCodeExpiry = Duration.ofMinutes(10);

    private int verificationCodeLength = 6;

    private int maxVerificationAttempts = 5;

    private int resendCooldownSeconds = 60;

}