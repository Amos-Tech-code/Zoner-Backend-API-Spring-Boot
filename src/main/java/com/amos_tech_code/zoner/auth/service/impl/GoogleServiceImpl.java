package com.amos_tech_code.zoner.auth.service.impl;

import com.amos_tech_code.zoner.auth.dto.internal.GooglePrincipal;
import com.amos_tech_code.zoner.auth.service.GoogleService;
import com.amos_tech_code.zoner.common.exception.InvalidCredentialsException;
import com.amos_tech_code.zoner.config.properties.GoogleProperties;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleServiceImpl implements GoogleService {

    private final GoogleProperties properties;

    @Override
    public GooglePrincipal verifyIdToken(
            String idToken
    ) {

        GoogleIdTokenVerifier verifier =
                new GoogleIdTokenVerifier.Builder(
                        new NetHttpTransport(),
                        GsonFactory.getDefaultInstance()
                )
                        .setAudience(
                                Collections.singletonList(
                                        properties.getClientId()
                                )
                        )
                        .build();

        try {

            GoogleIdToken token =
                    verifier.verify(idToken);

            if (token == null) {
                throw new InvalidCredentialsException(
                        "Invalid Google token."
                );
            }

            GoogleIdToken.Payload payload =
                    token.getPayload();

            return new GooglePrincipal(
                    payload.getSubject(),
                    payload.getEmail(),
                    payload.get("name").toString(),
                    payload.get("picture").toString()
            );

        } catch (Exception ex) {
            log.error("Google authentication failed: {}", ex.getMessage(), ex);
            throw new InvalidCredentialsException(
                    "Google authentication failed."
            );

        }

    }

}