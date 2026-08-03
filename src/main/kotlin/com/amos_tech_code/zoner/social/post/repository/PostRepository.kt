package com.amos_tech_code.zoner.social.post.repository

import com.amos_tech_code.zoner.business.entity.BusinessProfile
import com.amos_tech_code.zoner.social.post.entity.Post
import com.amos_tech_code.zoner.social.post.enums.PostStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PostRepository : JpaRepository<Post, UUID> {

    fun findByIdAndDeletedAtIsNull(
        id: UUID
    ): Optional<Post>

    fun findByBusinessAndStatusAndDeletedAtIsNullOrderByPublishedAtDesc(
        business: BusinessProfile,
        status: PostStatus,
        pageable: Pageable
    ): Page<Post>

    fun findByBusinessAndStatusAndDeletedAtIsNullOrderByUpdatedAtDesc(
        business: BusinessProfile,
        status: PostStatus,
        pageable: Pageable
    ): Page<Post>

}