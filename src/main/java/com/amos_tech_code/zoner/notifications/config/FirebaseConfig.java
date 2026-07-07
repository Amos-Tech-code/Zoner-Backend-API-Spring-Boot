package com.amos_tech_code.zoner.notifications.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp(
            @Value("${firebase.service-account-path}")
            Resource resource
    ) throws IOException {

        GoogleCredentials credentials =
                GoogleCredentials.fromStream(resource.getInputStream());

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);

        }

        return FirebaseApp.getInstance();

    }

}