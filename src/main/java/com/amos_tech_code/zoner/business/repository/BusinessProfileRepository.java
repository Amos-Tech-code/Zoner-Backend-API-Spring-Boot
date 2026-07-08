package com.amos_tech_code.zoner.business.repository;

import com.amos_tech_code.zoner.business.entity.BusinessProfile;
import com.amos_tech_code.zoner.users.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface BusinessProfileRepository
        extends JpaRepository<BusinessProfile, UUID>, JpaSpecificationExecutor<BusinessProfile> {

    Optional<BusinessProfile> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    Optional<BusinessProfile> findByIdAndDeletedAtIsNull(UUID id);


}
