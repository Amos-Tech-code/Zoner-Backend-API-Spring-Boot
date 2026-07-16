package com.amos_tech_code.zoner.users.service;

import com.amos_tech_code.zoner.users.dto.response.UserProfileResponse;

import java.util.UUID;

public interface UserService {

    UserProfileResponse getCurrentUser();

    void updateProfilePicture(UUID userId, UUID mediaId);

}
