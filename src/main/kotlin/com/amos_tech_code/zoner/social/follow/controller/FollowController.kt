package com.amos_tech_code.zoner.social.follow.controller

import com.amos_tech_code.zoner.security.AuthenticatedUser
import com.amos_tech_code.zoner.social.follow.dto.request.FollowRequest
import com.amos_tech_code.zoner.social.follow.dto.response.FollowResponse
import com.amos_tech_code.zoner.social.follow.dto.response.FollowStatsResponse
import com.amos_tech_code.zoner.social.follow.dto.response.FollowUserResponse
import com.amos_tech_code.zoner.social.follow.dto.response.FollowingResponse
import com.amos_tech_code.zoner.social.follow.enums.FollowTargetType
import com.amos_tech_code.zoner.social.follow.service.FollowService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/follows")
class FollowController(

    private val followService: FollowService

) {

    @PostMapping
    fun follow(

        @AuthenticationPrincipal user: AuthenticatedUser,

        @Valid
        @RequestBody request: FollowRequest

    ): FollowResponse {

        return followService.follow(
            user.id,
            request
        )

    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun unfollow(

        @AuthenticationPrincipal user: AuthenticatedUser,

        @Valid
        @RequestBody request: FollowRequest

    ) {

        followService.unfollow(
            user.id,
            request
        )

    }

    @GetMapping("/me")
    fun myStats(

        @AuthenticationPrincipal user: AuthenticatedUser

    ): FollowStatsResponse {

        return followService.getStats(
            user.id
        )

    }

    @GetMapping("/users/{userId}")
    fun userStats(

        @PathVariable userId: UUID

    ): FollowStatsResponse {

        return followService.getStats(
            userId
        )

    }

    @GetMapping("/status")
    fun isFollowing(

        @AuthenticationPrincipal user: AuthenticatedUser,

        @RequestParam targetType: FollowTargetType,

        @RequestParam targetId: UUID

    ): FollowResponse {

        return FollowResponse(
            followService.isFollowing(
                user.id,
                FollowRequest(
                    targetType,
                    targetId
                )
            )

        )

    }

    @GetMapping("/users/{id}/followers")
    fun followers(
        @PathVariable id: UUID,
        pageable: Pageable
    ): Page<FollowUserResponse> {

        return followService.getFollowers(
            FollowTargetType.USER,
            id,
            pageable
        )

    }

    @GetMapping("/businesses/{id}/followers")
    fun businessFollowers(
        @PathVariable id: UUID,
        pageable: Pageable
    ): Page<FollowUserResponse> {

        return followService.getFollowers(
            FollowTargetType.BUSINESS,
            id,
            pageable
        )

    }

    @GetMapping("/users/{id}/following")
    fun following(
        @PathVariable id: UUID,
        pageable: Pageable
    ): Page<FollowingResponse> {

        return followService.getFollowing(
            id,
            pageable
        )

    }

}