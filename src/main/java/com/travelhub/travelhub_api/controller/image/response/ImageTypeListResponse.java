package com.travelhub.travelhub_api.controller.image.response;

import com.travelhub.travelhub_api.data.enums.ImageType;
import com.travelhub.travelhub_api.data.mysql.entity.ImageEntity;
import lombok.Builder;

import java.util.List;

@Builder
public record ImageTypeListResponse(
        Long igIdx,
        Long idx,
        ImageType igType,
        String igUrl,
        String igPath,
        Long stIdx
) {

    public List<ImageTypeListResponse> ofList(List<ImageEntity> imageEntities) {
        return imageEntities.stream()
                .map(ImageTypeListResponse::of)
                .toList();
    }

    public static ImageTypeListResponse of(ImageEntity imageEntity) {
        return ImageTypeListResponse.builder()
                .igIdx(imageEntity.getIgIdx())
                .idx(imageEntity.getIdx())
                .igType(imageEntity.getIgType())
                .igUrl(imageEntity.getIgUrl())
                .igPath(imageEntity.getIgPath())
                .stIdx(imageEntity.getStIdx())
                .build();
    }
}
