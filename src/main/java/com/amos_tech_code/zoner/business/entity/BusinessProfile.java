package com.amos_tech_code.zoner.business.entity;

import com.amos_tech_code.zoner.common.entity.BaseEntity;
import com.amos_tech_code.zoner.users.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "business_profiles")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class BusinessProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "business_category_id",
            nullable = false
    )
    private BusinessCategory category;

    @Column(nullable = false, length = 150)
    private String businessName;

    @Column(length = 1000)
    private String description;

    @Column(length = 500)
    private String logoUrl;

    @Column(length = 500)
    private String coverPhotoUrl;

    @Column
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(length = 500)
    private String website;

    @Column(length = 500)
    private String address;

    @Column(length = 100)
    private String county;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String country;

    @Column(length = 20)
    private String postalCode;

    @Column(nullable = false)
    private boolean featured = false;

    @Column(nullable = false)
    private boolean acceptsMessages = true;

    @Column(nullable = false)
    private boolean verified = false;

}
