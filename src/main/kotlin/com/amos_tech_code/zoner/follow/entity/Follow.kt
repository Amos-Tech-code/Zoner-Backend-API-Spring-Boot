package com.amos_tech_code.zoner.follow.entity

import com.amos_tech_code.zoner.follow.enums.FollowTargetType
import com.amos_tech_code.zoner.users.entity.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.UuidGenerator
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "follows",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_follow_unique",
            columnNames = [
                "follower_id",
                "target_type",
                "target_id"
            ]
        )
    ],
    indexes = [
        Index(name = "idx_follow_follower", columnList = "follower_id"),
        Index(name = "idx_follow_target", columnList = "target_type,target_id")
    ]
)
class Follow(

    @Id
    @GeneratedValue
    @UuidGenerator
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    var follower: User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var targetType: FollowTargetType,

    @Column(nullable = false)
    var targetId: UUID,

    @Column(nullable = false)
    var createdAt: Instant = Instant.now()

)
