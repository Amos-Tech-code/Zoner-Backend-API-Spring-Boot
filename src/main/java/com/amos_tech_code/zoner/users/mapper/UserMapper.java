package com.amos_tech_code.zoner.users.mapper;

import com.amos_tech_code.zoner.users.dto.response.UserProfileResponse;
import com.amos_tech_code.zoner.users.entity.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserProfileResponse toUserResponse(User user) {

        String profilePictureUrl = null;
        if (user.getProfilePicture() != null) {
            profilePictureUrl = user.getProfilePicture().getSecureUrl();
        }

        return new UserProfileResponse(
                user.getId(),
                user.getDisplayName(),
                user.getUsername(),
                user.getBio(),
                profilePictureUrl,
                user.getPhone(),
                user.isEmailVerified(),
                user.getRole(),
                user.getRegistrationStage()
        );
    }
}
