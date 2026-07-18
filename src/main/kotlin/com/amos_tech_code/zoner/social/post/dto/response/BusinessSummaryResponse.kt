package com.amos_tech_code.zoner.social.post.dto.response

import java.util.UUID

data class BusinessSummary(

    val id: UUID,

    val businessName: String,

    val logoUrl: String?

)