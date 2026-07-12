package com.amos_tech_code.zoner.auth.service;

import com.amos_tech_code.zoner.auth.dto.internal.GooglePrincipal;

public interface GoogleService {

    GooglePrincipal verifyIdToken(
            String idToken
    );

}
