package com.amos_tech_code.zoner.business.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateBusinessProfileRequest(

        @NotBlank(message = "Business name is required")
        String businessName,

        String description,

        @NotNull(message = "Business category is required")
        UUID businessCategoryId,

        String phone,

        String email,

        String website,

        String address,

        String city,

        String county,

        String country,

        String postalCode,

        Boolean acceptsMessages

) {
}
