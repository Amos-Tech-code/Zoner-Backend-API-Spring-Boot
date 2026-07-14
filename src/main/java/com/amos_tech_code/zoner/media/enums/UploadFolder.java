package com.amos_tech_code.zoner.media.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UploadFolder {

    USER_PROFILE("users/profile-pictures"),

    BUSINESS_LOGO("business/logos"),

    BUSINESS_COVER("business/covers"),

    POST_IMAGE("posts/images"),

    POST_VIDEO("posts/videos"),

    STORY_IMAGE("stories/images"),

    STORY_VIDEO("stories/videos"),

    PRODUCT_IMAGE("products/images"),

    PRODUCT_VIDEO("products/videos"),

    MESSAGE_IMAGE("messages/images"),

    MESSAGE_VIDEO("messages/videos"),

    COMMENT_IMAGE("comments/images");

    private final String folder;

}
