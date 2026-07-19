package com.amos_tech_code.zoner.social.post.controller

import com.amos_tech_code.zoner.security.AuthenticatedUser
import com.amos_tech_code.zoner.social.post.dto.request.CreatePostRequest
import com.amos_tech_code.zoner.social.post.dto.request.UpdatePostRequest
import com.amos_tech_code.zoner.social.post.dto.response.PostResponse
import com.amos_tech_code.zoner.social.post.service.PostService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/posts")
class PostController(
    private val postService: PostService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(

        @AuthenticationPrincipal user: AuthenticatedUser,

        @Valid
        @RequestBody
        request: CreatePostRequest

    ): PostResponse {

        return postService.create(
            user.id(),
            request
        )

    }

    @PutMapping("/{postId}")
    fun update(

        @AuthenticationPrincipal user: AuthenticatedUser,

        @PathVariable postId: UUID,

        @Valid
        @RequestBody
        request: UpdatePostRequest

    ): PostResponse {

        return postService.update(
            user.id(),
            postId,
            request
        )

    }

    @PostMapping("/{postId}/publish")
    fun publish(

        @AuthenticationPrincipal user: AuthenticatedUser,

        @PathVariable postId: UUID

    ): PostResponse {

        return postService.publish(
            user.id(),
            postId
        )

    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(

        @AuthenticationPrincipal user: AuthenticatedUser,

        @PathVariable
        postId: UUID

    ) {

        postService.delete(
            user.id(),
            postId
        )

    }

    @GetMapping("/{postId}")
    fun get(

        @PathVariable
        postId: UUID

    ): PostResponse {

        return postService.get(
            postId
        )

    }

    @GetMapping("/drafts")
    fun getDrafts(

        @AuthenticationPrincipal user: AuthenticatedUser,

        pageable: Pageable

    ): Page<PostResponse> {

        return postService.getDrafts(
            user.id(),
            pageable
        )

    }

    @GetMapping("/{businessId}/posts")
    fun getBusinessPosts(

        @PathVariable
        businessId: UUID,

        pageable: Pageable

    ): Page<PostResponse> {

        return postService.getBusinessPosts(
            businessId,
            pageable
        )

    }

}