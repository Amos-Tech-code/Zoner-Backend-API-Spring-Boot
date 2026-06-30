package com.amos_tech_code.zoner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class TimeConfig {

    // This makes expiry logic easy to test.
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

}