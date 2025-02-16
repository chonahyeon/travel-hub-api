package com.travelhub.travelhub_api.data.dto.contents;

import com.travelhub.travelhub_api.data.enums.ContentsPlaceType;
import com.travelhub.travelhub_api.data.enums.ImageType;
import com.travelhub.travelhub_api.data.mysql.entity.ContentsPlaceEntity;
import com.travelhub.travelhub_api.data.mysql.entity.ImageEntity;

public record ContentsPlaceWriterDto(
        String pcId,
        String text,
        Integer cpOrder,
        String images // url 구분자 `,`
) {
    public ContentsPlaceEntity ofContentsPlaceEntity(Long ctIdx, Long pcIdx, Long igIdx) {
        return ContentsPlaceEntity.builder()
                .cpType(ContentsPlaceType.M)
                .cpOrder(this.cpOrder())
                .pcIdx(pcIdx)
                .cpText(this.text)
                .ctIdx(ctIdx)
                .igIdx(igIdx)
                .build();
    }

    public ImageEntity ofImageEntity(Long ctIdx) {
        return ImageEntity.builder()
                .idx(ctIdx)
                .stIdx(1L)
                .igType(ImageType.CT)
                .igPath(images)
                .build();
    }
}
