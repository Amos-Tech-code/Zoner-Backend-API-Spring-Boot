package com.amos_tech_code.zoner.social.follow.repository

import com.amos_tech_code.zoner.social.follow.entity.Follow
import com.amos_tech_code.zoner.social.follow.enums.FollowTargetType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface FollowRepository : JpaRepository<Follow, UUID> {

    fun existsByFollowerIdAndTargetTypeAndTargetId(
        followerId: UUID,
        targetType: FollowTargetType,
        targetId: UUID
    ): Boolean

    fun deleteByFollowerIdAndTargetTypeAndTargetId(
        followerId: UUID,
        targetType: FollowTargetType,
        targetId: UUID
    )

    fun countByTargetTypeAndTargetId(
        targetType: FollowTargetType,
        targetId: UUID
    ): Long

    fun countByFollowerId(
        followerId: UUID
    ): Long

    fun findByTargetTypeAndTargetId(
        targetType: FollowTargetType,
        targetId: UUID,
        pageable: Pageable
    ): Page<Follow>

    fun findByFollowerId(
        followerId: UUID,
        pageable: Pageable
    ): Page<Follow>

}