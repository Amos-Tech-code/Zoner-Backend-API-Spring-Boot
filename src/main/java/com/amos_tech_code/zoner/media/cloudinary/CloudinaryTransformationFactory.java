package com.amos_tech_code.zoner.media.cloudinary;

import com.amos_tech_code.zoner.media.enums.UploadFolder;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CloudinaryTransformationFactory {

    public Map<String, Object> create(
            UploadFolder folder
    ) {

        return switch (folder) {

            case USER_PROFILE -> avatar();

            case BUSINESS_LOGO -> businessLogo();

            case BUSINESS_COVER -> businessCover();

            case POST_IMAGE -> postImage();

            case STORY_IMAGE -> storyImage();

            case PRODUCT_IMAGE -> productImage();

            case MESSAGE_IMAGE,
                 COMMENT_IMAGE -> messageImage();

            case POST_VIDEO,
                 STORY_VIDEO,
                 PRODUCT_VIDEO,
                 MESSAGE_VIDEO -> video(folder);

        };

    }

    private Map<String, Object> avatar() {

        return ObjectUtils.asMap(

                "folder",
                UploadFolder.USER_PROFILE.getFolder(),

                "quality",
                "auto",

                "fetch_format",
                "auto",

                "gravity",
                "face",

                "crop",
                "fill",

                "width",
                400,

                "height",
                400

        );

    }

    private Map<String, Object> businessLogo() {

        return ObjectUtils.asMap(

                "folder",
                UploadFolder.BUSINESS_LOGO.getFolder(),

                "quality",
                "auto",

                "fetch_format",
                "auto",

                "crop",
                "fit",

                "width",
                600,

                "height",
                600

        );

    }

    private Map<String, Object> businessCover() {

        return ObjectUtils.asMap(
                "folder",
                UploadFolder.BUSINESS_COVER.getFolder(),
                "quality",
                "auto",
                "fetch_format",
                "auto",
                "crop",
                "fit",
                "width",
                1920,
                "height",
                1080
        );
    }

    private Map<String, Object> storyImage() {

        return ObjectUtils.asMap(

                "folder",
                UploadFolder.STORY_IMAGE.getFolder(),

                "quality",
                "auto",

                "fetch_format",
                "auto",

                "crop",
                "limit",

                "width",
                1080,

                "height",
                1920

        );

    }

    private Map<String, Object> postImage() {

        return ObjectUtils.asMap(

                "folder",
                UploadFolder.POST_IMAGE.getFolder(),

                "quality",
                "auto",

                "fetch_format",
                "auto",

                "crop",
                "limit",

                "width",
                1080,

                "height",
                1080

        );

    }

    private Map<String, Object> productImage() {
        return ObjectUtils.asMap(
                "folder",
                UploadFolder.PRODUCT_IMAGE.getFolder(),
                "quality",
                "auto",
                "fetch_format",
                "auto",
                "crop",
                "limit",
                "width",
                1080,
                "height",
                1080
        );
    }

    private Map<String, Object> messageImage() {
        return ObjectUtils.asMap(
                "folder",
                UploadFolder.MESSAGE_IMAGE.getFolder(),
                "quality",
                "auto",
                "fetch_format",
                "auto",
                "crop",
                "limit",
                "width",
                1080,
                "height",
                1080

        );
    }

    private Map<String, Object> video(
            UploadFolder folder
    ) {

        return ObjectUtils.asMap(

                "folder",
                folder.getFolder(),

                "resource_type",
                "video",

                "quality",
                "auto"

        );

    }

}
