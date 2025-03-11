package com.travelhub.travelhub_api.data.dto.storage;

import lombok.Builder;
import lombok.Getter;

import java.io.InputStream;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
public class UploadDTO {
    @Builder.Default
    private String uniqueKey = UUID.randomUUID().toString();

    private String uploadPath;

    private Long size;

    private InputStream uploadFileStream;
}
