package com.amos_tech_code.zoner.auth.mapper;

import com.amos_tech_code.zoner.auth.dto.response.RegisterResponse;
import com.amos_tech_code.zoner.users.entity.User;

public final class AuthMapper {

    private AuthMapper() {
    }

    public static RegisterResponse toRegisterResponse(
            User user,
            String message
    ) {

        return new RegisterResponse(
                user.getId(),
                user.getEmail(),
                user.getRegistrationStage(),
                message
        );

    }

}
