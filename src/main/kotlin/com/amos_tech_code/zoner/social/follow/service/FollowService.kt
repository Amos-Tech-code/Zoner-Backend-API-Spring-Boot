package com.amos_tech_code.zoner.social.follow.service

import com.amos_tech_code.zoner.social.follow.dto.request.FollowRequest
import com.amos_tech_code.zoner.social.follow.dto.response.FollowResponse
import com.amos_tech_code.zoner.social.follow.dto.response.FollowStatsResponse
import com.amos_tech_code.zoner.social.follow.dto.response.FollowUserResponse
import com.amos_tech_code.zoner.social.follow.dto.response.FollowingResponse
import com.amos_tech_code.zoner.social.follow.enums.FollowTargetType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface FollowService {

    fun follow(
        followerId: UUID,
        request: FollowRequest
    ): FollowResponse

    fun unfollow(
        followerId: UUID,
        request: FollowRequest
    )

    fun getStats(
        userId: UUID
    ): FollowStatsResponse

    fun isFollowing(
        followerId: UUID,
        request: FollowRequest
    ): Boolean

    fun getFollowers(
        targetType: FollowTargetType,
        targetId: UUID,
        pageable: Pageable
    ): Page<FollowUserResponse>

    fun getFollowing(
        followerId: UUID,
        pageable: Pageable
    ): Page<FollowingResponse>

}