package com.amos_tech_code.zoner.users.mapper;

import com.amos_tech_code.zoner.users.dto.response.UserProfileResponse;
import com.amos_tech_code.zoner.users.entity.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserProfileResponse toResponse(User user) {

        return new UserProfileResponse(
                user.getId(),
                user.getDisplayName(),
                user.getUsername(),
                user.getBio(),
                user.getProfilePicture().getUrl(),
                user.getPhone(),
                user.isEmailVerified()
        );
    }
}
