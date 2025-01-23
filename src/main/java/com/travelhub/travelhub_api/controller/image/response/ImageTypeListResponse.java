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

    public static List<ImageTypeListResponse> ofList(List<ImageEntity> imageEntities, String domain) {
        return imageEntities.stream()
                .map(imageEntity -> of(imageEntity, domain))
                .toList();
    }

    public static ImageTypeListResponse of(ImageEntity imageEntity, String domain) {
        String imageUrl = domain + imageEntity.getIgPath();

        return ImageTypeListResponse.builder()
                .igIdx(imageEntity.getIgIdx())
                .idx(imageEntity.getIdx())
                .igType(imageEntity.getIgType())
                .igUrl(imageUrl)
                .igPath(imageEntity.getIgPath())
                .stIdx(imageEntity.getStIdx())
                .build();
    }
}
