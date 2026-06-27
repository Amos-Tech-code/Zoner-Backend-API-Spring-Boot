package com.amos_tech_code.zoner.business.entity;

import com.amos_tech_code.zoner.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "business_categories")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class BusinessCategory extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 300)
    private String description;

    @Column(length = 500)
    private String iconUrl;

    @Column(nullable = false)
    private boolean active = true;

}
