package com.travelhub.travelhub_api.data.dto.contents;

import com.travelhub.travelhub_api.data.mysql.entity.ContentsEntity;
import com.travelhub.travelhub_api.data.mysql.entity.ContentsPlaceEntity;
import com.travelhub.travelhub_api.data.mysql.entity.ImageEntity;
import com.travelhub.travelhub_api.data.mysql.entity.PlaceEntity;

public record ContentsSummaryDto (
        ContentsEntity contents,
        ContentsPlaceEntity contentsPlace,
        PlaceEntity place,
        ImageEntity image
) {
    public ContentsPlaceDto ofContentsPlace() {
        return ContentsPlaceDto.builder()
                .contentsPlace(this.contentsPlace())
                .place(this.place())
                .image(this.image())
                .build();
    }
}
