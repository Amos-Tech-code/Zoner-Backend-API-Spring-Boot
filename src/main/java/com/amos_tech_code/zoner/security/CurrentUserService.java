package com.amos_tech_code.zoner.security;

import java.util.UUID;

public interface CurrentUserService {

    AuthenticatedUser getAuthenticatedUser();

    UUID getCurrentUserId();

}
