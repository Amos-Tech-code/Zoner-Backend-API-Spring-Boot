package com.amos_tech_code.zoner.business.repository;

import com.amos_tech_code.zoner.business.entity.BusinessProfile;
import com.amos_tech_code.zoner.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BusinessProfileRepository extends JpaRepository<BusinessProfile, UUID> {

    Optional<BusinessProfile> findByUser(User user);

    boolean existsByUser(User user);

}
