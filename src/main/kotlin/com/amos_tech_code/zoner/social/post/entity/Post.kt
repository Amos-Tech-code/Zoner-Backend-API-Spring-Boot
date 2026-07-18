package com.amos_tech_code.zoner.social.post.entity

import com.amos_tech_code.zoner.business.entity.BusinessProfile
import com.amos_tech_code.zoner.common.entity.BaseEntity
import com.amos_tech_code.zoner.social.post.enums.PostStatus
import com.amos_tech_code.zoner.social.post.enums.PostVisibility
import com.amos_tech_code.zoner.users.entity.User
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(
    name = "posts",
    indexes = [
        Index(name = "idx_posts_business", columnList = "business_id"),
        Index(name = "idx_posts_status", columnList = "status"),
        Index(name = "idx_posts_visibility", columnList = "visibility"),
        Index(name = "idx_posts_published", columnList = "published_at"),
        Index(name = "idx_posts_deleted", columnList = "deleted_at")
    ]
)
class Post(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    var business: BusinessProfile? = null,

    @Column(columnDefinition = "TEXT")
    var caption: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var visibility: PostVisibility = PostVisibility.PUBLIC,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: PostStatus = PostStatus.DRAFT,

    @Column(nullable = false)
    var commentsEnabled: Boolean = true,

    @Column(nullable = false)
    var allowSharing: Boolean = true,

    @Column
    var editedAt: Instant? = null,

    @Column
    var publishedAt: Instant? = null

) : BaseEntity()