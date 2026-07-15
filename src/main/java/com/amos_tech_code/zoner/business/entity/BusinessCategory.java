package com.amos_tech_code.zoner.business.entity;

import com.amos_tech_code.zoner.common.entity.BaseEntity;
import com.amos_tech_code.zoner.media.entity.Media;
import jakarta.persistence.*;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "icon_media_id")
    private Media icon;

    @Column(nullable = false)
    private boolean active = true;

}
