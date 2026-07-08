package com.amos_tech_code.zoner.business.mapper;

import com.amos_tech_code.zoner.business.dto.response.BusinessCategoryResponse;
import com.amos_tech_code.zoner.business.dto.response.BusinessProfileResponse;
import com.amos_tech_code.zoner.business.entity.BusinessCategory;
import com.amos_tech_code.zoner.business.entity.BusinessProfile;

public final class BusinessMapper {

    private BusinessMapper() {
    }

    public static BusinessCategoryResponse toResponse(
            BusinessCategory category
    ) {

        return new BusinessCategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getIconUrl()
        );

    }

    public static BusinessProfileResponse toResponse(
            BusinessProfile business
    ) {

        return new BusinessProfileResponse(
                business.getId(),
                business.getUser().getId(),
                business.getCategory().getId(),
                business.getCategory().getName(),
                business.getBusinessName(),
                business.getDescription(),
                business.getLogoUrl(),
                business.getCoverPhotoUrl(),
                business.getPhone(),
                business.getEmail(),
                business.getWebsite(),
                business.getAddress(),
                business.getCity(),
                business.getCounty(),
                business.getCountry(),
                business.getPostalCode(),
                business.isFeatured(),
                business.isVerified(),
                business.isAcceptsMessages()
        );

    }

}
