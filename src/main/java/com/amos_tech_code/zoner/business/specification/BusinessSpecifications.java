package com.amos_tech_code.zoner.business.specification;

import com.amos_tech_code.zoner.business.entity.BusinessProfile;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class BusinessSpecifications {

    private BusinessSpecifications() {
    }

    public static Specification<BusinessProfile> active() {

        return (root, query, cb) ->
                cb.isNull(root.get("deletedAt"));

    }

    public static Specification<BusinessProfile> nameContains(
            String keyword
    ) {

        return (root, query, cb) ->

                keyword == null || keyword.isBlank()

                        ? null

                        : cb.like(
                        cb.lower(root.get("businessName")),
                        "%" + keyword.toLowerCase() + "%"
                );

    }

    public static Specification<BusinessProfile> category(
            UUID categoryId
    ) {

        return (root, query, cb) ->

                categoryId == null

                        ? null

                        : cb.equal(
                        root.get("category").get("id"),
                        categoryId
                );

    }

    public static Specification<BusinessProfile> city(
            String city
    ) {

        return (root, query, cb) ->

                city == null || city.isBlank()

                        ? null

                        : cb.equal(
                        cb.lower(root.get("city")),
                        city.toLowerCase()
                );

    }

    public static Specification<BusinessProfile> county(
            String county
    ) {

        return (root, query, cb) ->

                county == null || county.isBlank()

                        ? null

                        : cb.equal(
                        cb.lower(root.get("county")),
                        county.toLowerCase()
                );

    }

    public static Specification<BusinessProfile> verified(
            Boolean verified
    ) {

        return (root, query, cb) ->

                verified == null

                        ? null

                        : cb.equal(
                        root.get("verified"),
                        verified
                );

    }

    public static Specification<BusinessProfile> featured(
            Boolean featured
    ) {

        return (root, query, cb) ->

                featured == null

                        ? null

                        : cb.equal(
                        root.get("featured"),
                        featured
                );

    }

}
