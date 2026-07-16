package com.amos_tech_code.zoner.users.service.impl;

import com.amos_tech_code.zoner.common.exception.ResourceNotFoundException;
import com.amos_tech_code.zoner.media.entity.Media;
import com.amos_tech_code.zoner.media.enums.MediaOwnerType;
import com.amos_tech_code.zoner.media.service.MediaService;
import com.amos_tech_code.zoner.security.CurrentUserService;
import com.amos_tech_code.zoner.users.dto.response.UserProfileResponse;
import com.amos_tech_code.zoner.users.entity.User;
import com.amos_tech_code.zoner.users.mapper.UserMapper;
import com.amos_tech_code.zoner.users.repository.UserRepository;
import com.amos_tech_code.zoner.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final CurrentUserService currentUserService;

    private final MediaService mediaService;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUser() {

        User user =
                userRepository
                        .findByIdAndDeletedAtIsNull(currentUserService.getCurrentUserId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found."
                                )
                        );

        return UserMapper.toUserResponse(user);

    }

    @Override
    @Transactional
    public void updateProfilePicture(
            UUID userId,
            UUID mediaId
    ) {

        User user =
                userRepository.findByIdAndDeletedAtIsNull(userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("User not found."));

        Media media = mediaService.attach(
                mediaId,
                MediaOwnerType.USER,
                user.getId()
        );

        Media previous = user.getProfilePicture();

        user.setProfilePicture(media);

        userRepository.save(user);

        if (previous != null) {
            mediaService.delete(previous.getId());
        }

    }

}
