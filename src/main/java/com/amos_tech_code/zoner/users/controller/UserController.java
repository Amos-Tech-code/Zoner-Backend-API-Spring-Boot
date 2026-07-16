package com.amos_tech_code.zoner.users.controller;

import com.amos_tech_code.zoner.media.dto.request.AttachMediaRequest;
import com.amos_tech_code.zoner.security.AuthenticatedUser;
import com.amos_tech_code.zoner.users.dto.response.UserProfileResponse;
import com.amos_tech_code.zoner.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserProfileResponse me() {

        return userService.getCurrentUser();

    }

    @PutMapping("/me/profile-picture")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProfilePicture(

            @AuthenticationPrincipal AuthenticatedUser user,

            @Valid
            @RequestBody AttachMediaRequest request

    ) {

        userService.updateProfilePicture(
                user.id(),
                request.mediaId()
        );

    }

}
