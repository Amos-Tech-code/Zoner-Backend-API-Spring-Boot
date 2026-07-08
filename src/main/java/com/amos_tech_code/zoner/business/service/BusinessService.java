package com.amos_tech_code.zoner.business.service;

import com.amos_tech_code.zoner.business.dto.request.CreateBusinessProfileRequest;
import com.amos_tech_code.zoner.business.dto.request.SearchBusinessesRequest;
import com.amos_tech_code.zoner.business.dto.request.UpdateBusinessProfileRequest;
import com.amos_tech_code.zoner.business.dto.response.BusinessCategoryResponse;
import com.amos_tech_code.zoner.business.dto.response.BusinessProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface BusinessService {

    BusinessProfileResponse create(
            UUID userId,
            CreateBusinessProfileRequest request
    );

    BusinessProfileResponse update(
            UUID userId,
            UpdateBusinessProfileRequest request
    );

    BusinessProfileResponse getMine(
            UUID userId
    );

    BusinessProfileResponse getById(
            UUID businessId
    );

    List<BusinessCategoryResponse> getCategories();

    Page<BusinessProfileResponse> search(
            SearchBusinessesRequest request,
            Pageable pageable
    );

    void deleteBusiness(UUID userId);

}