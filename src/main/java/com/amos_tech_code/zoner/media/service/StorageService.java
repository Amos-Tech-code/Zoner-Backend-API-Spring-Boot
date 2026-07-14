package com.amos_tech_code.zoner.media.service;

import com.amos_tech_code.zoner.media.dto.internal.UploadOptions;
import com.amos_tech_code.zoner.media.dto.internal.StoredMedia;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    StoredMedia upload(
            MultipartFile file,
            UploadOptions options
    );

    void delete(
            String publicId
    );

}
