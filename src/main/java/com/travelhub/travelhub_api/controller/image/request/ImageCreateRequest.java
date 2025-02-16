package com.travelhub.travelhub_api.controller.image.request;

import com.travelhub.travelhub_api.data.enums.ImageType;
import com.travelhub.travelhub_api.data.mysql.entity.ImageEntity;

public record ImageCreateRequest(
        Long ctIdx,
        ImageType igType,
        String igUrl
) {
    public ImageEntity of() {
        return ImageEntity.builder()
                .idx(this.ctIdx)
                .igType(igType)
                .igPath(igUrl)
                .build();
    }
}
