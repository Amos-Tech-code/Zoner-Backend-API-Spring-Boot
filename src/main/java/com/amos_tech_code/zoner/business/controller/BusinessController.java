package com.amos_tech_code.zoner.business.controller;

import com.amos_tech_code.zoner.business.dto.request.CreateBusinessProfileRequest;
import com.amos_tech_code.zoner.business.dto.request.SearchBusinessesRequest;
import com.amos_tech_code.zoner.business.dto.request.UpdateBusinessProfileRequest;
import com.amos_tech_code.zoner.business.dto.response.BusinessCategoryResponse;
import com.amos_tech_code.zoner.business.dto.response.BusinessProfileResponse;
import com.amos_tech_code.zoner.business.service.BusinessService;
import com.amos_tech_code.zoner.security.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    @PostMapping("/profile")
    @ResponseStatus(HttpStatus.CREATED)
    public BusinessProfileResponse createProfile(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody CreateBusinessProfileRequest request
    ) {

        return businessService.create(
                user.id(),
                request
        );

    }

    @GetMapping("/profile/me")
    public BusinessProfileResponse getMyProfile(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {

        return businessService.getMine(
                user.id()
        );

    }

    @PutMapping("/profile")
    public BusinessProfileResponse updateProfile(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody UpdateBusinessProfileRequest request
    ) {

        return businessService.update(
                user.id(),
                request
        );

    }

    @GetMapping("/{businessId}")
    public BusinessProfileResponse getBusiness(
            @PathVariable UUID businessId
    ) {

        return businessService.getById(
                businessId
        );

    }

    @GetMapping("/categories")
    public List<BusinessCategoryResponse> getCategories() {

        return businessService.getCategories();

    }

    @PostMapping("/search")
    public Page<BusinessProfileResponse> search(
            @RequestBody SearchBusinessesRequest request,
            Pageable pageable
    ) {

        return businessService.search(request, pageable);

    }

}