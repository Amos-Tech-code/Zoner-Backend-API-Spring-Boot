package com.amos_tech_code.zoner.social.post.repository

import com.amos_tech_code.zoner.business.entity.BusinessProfile
import com.amos_tech_code.zoner.social.post.entity.Post
import com.amos_tech_code.zoner.social.post.enums.PostStatus
import com.amos_tech_code.zoner.users.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PostRepository : JpaRepository<Post, UUID> {

    fun findByIdAndDeletedAtIsNull(
        id: UUID
    ): Optional<Post>

    fun findByBusinessAndStatusAndDeletedAtIsNullOrderByPublishedAtDesc(
        business: BusinessProfile,
        status: PostStatus,
        pageable: Pageable
    ): Page<Post>

    fun findByBusinessAndDeletedAtIsNullOrderByUpdatedAtDesc(
        business: BusinessProfile,
        pageable: Pageable
    ): Page<Post>

}