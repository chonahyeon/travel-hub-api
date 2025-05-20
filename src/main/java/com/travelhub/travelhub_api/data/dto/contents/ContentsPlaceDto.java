package com.travelhub.travelhub_api.data.dto.contents;

import com.travelhub.travelhub_api.data.mysql.entity.ContentsPlaceEntity;
import com.travelhub.travelhub_api.data.mysql.entity.ImageEntity;
import com.travelhub.travelhub_api.data.mysql.entity.PlaceEntity;
import lombok.Builder;

@Builder
public record ContentsPlaceDto(
        ContentsPlaceEntity contentsPlace,
        PlaceEntity place,
        ImageEntity image
) {
}
