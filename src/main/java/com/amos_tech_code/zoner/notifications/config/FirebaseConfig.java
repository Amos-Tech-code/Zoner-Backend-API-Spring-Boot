package com.amos_tech_code.zoner.notifications.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp(
            @Value("${firebase.service-account-key}") // Inject the JSON string from application.properties
            String serviceAccountKeyJson
    ) throws IOException {

        if (serviceAccountKeyJson == null || serviceAccountKeyJson.isEmpty()) {
            throw new IllegalArgumentException("Firebase service account key (firebase.service-account-key) is not set in application.properties or environment.");
        }

        InputStream serviceAccount = new ByteArrayInputStream(serviceAccountKeyJson.getBytes(StandardCharsets.UTF_8));

        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }

        return FirebaseApp.getInstance();
    }
}
