package com.amos_tech_code.zoner.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    @NotBlank
    private String secret;

    @DurationUnit(ChronoUnit.MINUTES)
    private Duration accessTokenExpiration;

    @DurationUnit(ChronoUnit.DAYS)
    private Duration refreshTokenExpiration;

    @NotBlank
    private String issuer;
}
