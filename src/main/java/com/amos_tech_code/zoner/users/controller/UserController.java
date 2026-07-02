package com.amos_tech_code.zoner.users.controller;

import com.amos_tech_code.zoner.auth.dto.response.UserResponse;
import com.amos_tech_code.zoner.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse me() {

        return userService.getCurrentUser();

    }

}
