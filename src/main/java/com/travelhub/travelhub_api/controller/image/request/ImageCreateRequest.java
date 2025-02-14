package com.travelhub.travelhub_api.controller.image.request;

import com.travelhub.travelhub_api.data.enums.ImageType;

public record ImageCreateRequest(
        ImageType igType,
        Long idx
) {
}
