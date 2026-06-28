package com.amos_tech_code.zoner.business.repository;

import com.amos_tech_code.zoner.business.entity.BusinessCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BusinessCategoryRepository extends JpaRepository<BusinessCategory, UUID> {

    Optional<BusinessCategory> findByName(String name);

    List<BusinessCategory> findByActiveTrue();

}
