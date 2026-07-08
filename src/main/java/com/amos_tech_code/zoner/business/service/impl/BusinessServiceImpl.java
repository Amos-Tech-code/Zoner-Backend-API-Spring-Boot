package com.amos_tech_code.zoner.business.service.impl;

import com.amos_tech_code.zoner.business.dto.request.CreateBusinessProfileRequest;
import com.amos_tech_code.zoner.business.dto.request.SearchBusinessesRequest;
import com.amos_tech_code.zoner.business.dto.request.UpdateBusinessProfileRequest;
import com.amos_tech_code.zoner.business.dto.response.BusinessCategoryResponse;
import com.amos_tech_code.zoner.business.dto.response.BusinessProfileResponse;
import com.amos_tech_code.zoner.business.entity.BusinessCategory;
import com.amos_tech_code.zoner.business.entity.BusinessProfile;
import com.amos_tech_code.zoner.business.mapper.BusinessMapper;
import com.amos_tech_code.zoner.business.repository.BusinessCategoryRepository;
import com.amos_tech_code.zoner.business.repository.BusinessProfileRepository;
import com.amos_tech_code.zoner.business.service.BusinessService;
import com.amos_tech_code.zoner.business.specification.BusinessSpecifications;
import com.amos_tech_code.zoner.common.exception.DuplicateResourceException;
import com.amos_tech_code.zoner.common.exception.ResourceNotFoundException;
import com.amos_tech_code.zoner.users.entity.User;
import com.amos_tech_code.zoner.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BusinessServiceImpl implements BusinessService {

    private final BusinessProfileRepository businessRepository;

    private final BusinessCategoryRepository categoryRepository;

    private final UserRepository userRepository;

    private final Clock clock;

    @Override
    public BusinessProfileResponse create(
            UUID userId,
            CreateBusinessProfileRequest request
    ) {

        if (businessRepository.existsByUserId(userId)) {
            throw new DuplicateResourceException(
                    "Business profile already exists."
            );
        }

        User user = getUser(userId);

        BusinessCategory category =
                getCategory(request.businessCategoryId());

        BusinessProfile profile =
                BusinessProfile.builder()
                        .user(user)
                        .category(category)
                        .businessName(request.businessName())
                        .description(request.description())
                        .phone(request.phone())
                        .email(request.email())
                        .website(request.website())
                        .address(request.address())
                        .city(request.city())
                        .county(request.county())
                        .country(request.country())
                        .postalCode(request.postalCode())
                        .acceptsMessages(true)
                        .featured(false)
                        .verified(false)
                        .build();

        businessRepository.save(profile);

        return BusinessMapper.toResponse(profile);

    }

    @Override
    public BusinessProfileResponse update(
            UUID userId,
            UpdateBusinessProfileRequest request
    ) {

        BusinessProfile profile = getBusiness(userId);

        BusinessCategory category =
                getCategory(request.businessCategoryId());

        profile.setBusinessName(request.businessName());
        profile.setDescription(request.description());
        profile.setCategory(category);
        profile.setPhone(request.phone());
        profile.setEmail(request.email());
        profile.setWebsite(request.website());
        profile.setAddress(request.address());
        profile.setCity(request.city());
        profile.setCounty(request.county());
        profile.setCountry(request.country());
        profile.setPostalCode(request.postalCode());
        profile.setAcceptsMessages(request.acceptsMessages());

        businessRepository.save(profile);

        return BusinessMapper.toResponse(profile);

    }

    @Override
    @Transactional(readOnly = true)
    public BusinessProfileResponse getMine(UUID userId) {

        return BusinessMapper.toResponse(
                getBusiness(userId)
        );

    }

    @Override
    @Transactional(readOnly = true)
    public BusinessProfileResponse getById(UUID businessId) {

        BusinessProfile profile =
                businessRepository
                        .findByIdAndDeletedAtIsNull(businessId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Business profile not found."
                                ));

        return BusinessMapper.toResponse(profile);

    }

    @Override
    @Transactional(readOnly = true)
    public List<BusinessCategoryResponse> getCategories() {

        return categoryRepository
                .findByActiveTrue()
                .stream()
                .map(BusinessMapper::toResponse)
                .toList();

    }

    @Override
    public Page<BusinessProfileResponse> search(
            SearchBusinessesRequest request,
            Pageable pageable
    ) {

        Specification<BusinessProfile> specification =
                Specification.where(BusinessSpecifications.active())
                        .and(BusinessSpecifications.nameContains(request.query()))
                        .and(BusinessSpecifications.category(request.categoryId()))
                        .and(BusinessSpecifications.city(request.city()))
                        .and(BusinessSpecifications.county(request.county()))
                        .and(BusinessSpecifications.verified(request.verified()))
                        .and(BusinessSpecifications.featured(request.featured()));

        return businessRepository
                .findAll(specification, pageable)
                .map(BusinessMapper::toResponse);
    }

    @Override
    public void deleteBusiness(UUID userId) {

        businessRepository
                .findByUserId(userId)
                .ifPresent(business -> {

                    business.setDeletedAt(
                            Instant.now(clock)
                    );

                    businessRepository.save(business);

                });

    }

    private User getUser(UUID userId) {

        return userRepository
                .findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found."
                        ));

    }

    private BusinessCategory getCategory(UUID categoryId) {

        return categoryRepository
                .findById(categoryId)
                .filter(BusinessCategory::isActive)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Business category not found."
                        ));

    }

    private BusinessProfile getBusiness(UUID userId) {

        return businessRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Business profile not found."
                        ));

    }

}