package com.amos_tech_code.zoner.users.service.impl;

import com.amos_tech_code.zoner.auth.dto.response.UserResponse;
import com.amos_tech_code.zoner.auth.mapper.AuthMapper;
import com.amos_tech_code.zoner.common.exception.ResourceNotFoundException;
import com.amos_tech_code.zoner.security.CurrentUserService;
import com.amos_tech_code.zoner.users.entity.User;
import com.amos_tech_code.zoner.users.repository.UserRepository;
import com.amos_tech_code.zoner.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final CurrentUserService currentUserService;

    @Override
    public UserResponse getCurrentUser() {

        User user =
                userRepository
                        .findByIdAndDeletedAtIsNull(
                                currentUserService.getCurrentUserId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found."
                                )
                        );

        return AuthMapper.toUserResponse(user);

    }

}
