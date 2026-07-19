package com.amos_tech_code.zoner.media.controller;

import com.amos_tech_code.zoner.media.dto.internal.UploadOptions;
import com.amos_tech_code.zoner.media.dto.request.UploadMediaRequest;
import com.amos_tech_code.zoner.media.dto.response.MediaResponse;
import com.amos_tech_code.zoner.media.enums.MediaResourceType;
import com.amos_tech_code.zoner.media.service.MediaService;
import com.amos_tech_code.zoner.security.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<MediaResponse> upload(

            @AuthenticationPrincipal AuthenticatedUser user,

            @RequestPart("file")
            MultipartFile file,

            @Valid
            @RequestPart("request")
            UploadMediaRequest request

    ) {

        UploadOptions options =
                new UploadOptions(
                        request.folder(),
                        request.ownerType(),
                        request.ownerId(),
                        request.resourceType(),
                        Optional.ofNullable(request.displayOrder()).orElse(0)
                );

        return ResponseEntity.ok(
                mediaService.upload(file, options, user.id())
        );

    }

    @GetMapping("/{id}")
    public ResponseEntity<MediaResponse> get(
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                mediaService.getById(id)
        );

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID id
    ) {

        mediaService.delete(id);

    }

}
