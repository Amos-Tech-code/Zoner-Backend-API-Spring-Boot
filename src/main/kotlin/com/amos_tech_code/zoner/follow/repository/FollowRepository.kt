package com.amos_tech_code.zoner.follow.repository

import com.amos_tech_code.zoner.follow.entity.Follow
import com.amos_tech_code.zoner.follow.enums.FollowTargetType
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

    fun findAllByFollowerId(
        followerId: UUID
    ): List<Follow>

    fun findAllByTargetTypeAndTargetId(
        targetType: FollowTargetType,
        targetId: UUID
    ): List<Follow>

}