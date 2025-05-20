package com.travelhub.travelhub_api.data.dto.contents;

import com.travelhub.travelhub_api.data.enums.ContentsPlaceType;
import com.travelhub.travelhub_api.data.enums.ImageType;
import com.travelhub.travelhub_api.data.mysql.entity.ContentsPlaceEntity;
import com.travelhub.travelhub_api.data.mysql.entity.ImageEntity;

import java.util.List;
import java.util.stream.IntStream;

public record ContentsPlaceWriterDto(
        String pcId,
        String text,
        Integer cpOrder,
        List<String> images,
        Long cpIdx
) {
    public ContentsPlaceEntity ofContentsPlaceEntity(Long ctIdx, Long pcIdx) {
        return ContentsPlaceEntity.builder()
                .cpType(ContentsPlaceType.M)
                .cpOrder(this.cpOrder())
                .pcIdx(pcIdx)
                .cpText(this.text)
                .ctIdx(ctIdx)
                .build();
    }

    public ImageEntity ofImageEntity(Long cpIdx) {
        return ImageEntity.builder()
                .idx(cpIdx)
                .stIdx(1L)
                .igType(ImageType.CT)
                .igPath(convertImages())
                .build();
    }

    public String convertImages() {
        StringBuilder sb = new StringBuilder();
        IntStream.range(0, images.size()).forEachOrdered(i -> {
            sb.append(images.get(i));

            if (i < images.size() - 1) {
                sb.append(",");
            }
        });

        return sb.toString();
    }
}
