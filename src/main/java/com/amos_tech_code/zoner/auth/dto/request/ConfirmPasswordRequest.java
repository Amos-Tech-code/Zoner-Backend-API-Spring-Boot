package com.amos_tech_code.zoner.auth.dto.request;


import jakarta.validation.constraints.NotBlank;

public record ConfirmPasswordRequest(

        @NotBlank
        String password

) {
}
