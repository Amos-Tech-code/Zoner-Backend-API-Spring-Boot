package com.amos_tech_code.zoner.social.post.service

import com.amos_tech_code.zoner.social.post.dto.request.CreatePostRequest
import com.amos_tech_code.zoner.social.post.dto.request.UpdatePostRequest
import com.amos_tech_code.zoner.social.post.dto.response.PostResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface PostService {

    fun create(
        userId: UUID,
        request: CreatePostRequest
    ): PostResponse

    fun update(
        userId: UUID,
        postId: UUID,
        request: UpdatePostRequest
    ): PostResponse

    fun publish(
        userId: UUID,
        postId: UUID
    ): PostResponse

    fun delete(
        userId: UUID,
        postId: UUID
    )

    fun get(
        postId: UUID
    ): PostResponse

    fun getBusinessPosts(
        businessId: UUID,
        pageable: Pageable
    ): Page<PostResponse>

    fun getDrafts(
        userId: UUID,
        pageable: Pageable
    ): Page<PostResponse>

}