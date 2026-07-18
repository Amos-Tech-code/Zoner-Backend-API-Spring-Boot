package com.amos_tech_code.zoner.social.post.mapper

import com.amos_tech_code.zoner.media.entity.Media
import com.amos_tech_code.zoner.social.post.dto.response.BusinessSummary
import com.amos_tech_code.zoner.social.post.dto.response.PostMediaResponse
import com.amos_tech_code.zoner.social.post.dto.response.PostResponse
import com.amos_tech_code.zoner.social.post.entity.Post

object PostMapper {

    fun toResponse(
        post: Post,
        media: List<Media>
    ): PostResponse {

        return PostResponse(

            id = post.id,

            business = BusinessSummary(

                id = post.business!!.id,

                businessName = post.business!!.businessName,

                logoUrl = post.business!!.logo?.secureUrl

            ),

            caption = post.caption,

            visibility = post.visibility,

            status = post.status,

            commentsEnabled = post.commentsEnabled,

            allowSharing = post.allowSharing,

            media = media
                .sortedBy { it.displayOrder }
                .map {

                    PostMediaResponse(

                        id = it.id,

                        secureUrl = it.secureUrl,

                        resourceType = it.resourceType,

                        width = it.width,

                        height = it.height,

                        duration = it.duration,

                        displayOrder = it.displayOrder

                    )

                },

            createdAt = post.createdAt,

            updatedAt = post.updatedAt,

            editedAt = post.editedAt,

            publishedAt = post.publishedAt

        )

    }

}