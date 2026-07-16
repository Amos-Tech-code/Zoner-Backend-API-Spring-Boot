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

        String iconUrl = null;
        if (category.getIcon() != null) {
            iconUrl = category.getIcon().getSecureUrl();
        }
        return new BusinessCategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                iconUrl
        );

    }

    public static BusinessProfileResponse toResponse(
            BusinessProfile business
    ) {

        String logoUrl = null;
        if (business.getLogo() != null) {
            logoUrl = business.getLogo().getSecureUrl();
        }

        String coverUrl = null;
        if (business.getCover() != null) {
            coverUrl = business.getCover().getSecureUrl();
        }

        return new BusinessProfileResponse(
                business.getId(),
                business.getUser().getId(),
                business.getCategory().getId(),
                business.getCategory().getName(),
                business.getBusinessName(),
                business.getDescription(),
                logoUrl,
                coverUrl,
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
