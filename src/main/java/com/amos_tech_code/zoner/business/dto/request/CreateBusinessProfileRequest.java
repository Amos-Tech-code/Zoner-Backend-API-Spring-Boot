package com.amos_tech_code.zoner.business.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateBusinessProfileRequest(

        @NotBlank
        String businessName,

        String description,

        @NotNull
        UUID businessCategoryId,

        String phone,

        String email,

        String website,

        String address,

        String city,

        String county,

        String country,

        String postalCode

) {}