package com.travelhub.travelhub_api.data.dto.contents;

import com.travelhub.travelhub_api.data.dto.image.ImagePathsDto;
import com.travelhub.travelhub_api.data.enums.ContentsPlaceType;
import com.travelhub.travelhub_api.data.mysql.entity.ContentsEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record ContentsPlaceReaderDto(
        ContentsEntity contents,
        Long cpIdx,
        Integer cpOrder,
        ContentsPlaceType cpType,
        String cpText,
        Long pcIdx,
        String pcId,
        Double pcLng,
        Double pcLat,
        String pcName,
        Double pcRating,
        String igPath
) {
    public PlaceDto ofPlace(String domain) {

        List<ImagePathsDto> images = new ArrayList<>();

        if (null != this.igPath) {
            images = Arrays.stream(this.igPath.split(","))
                    .map(path -> ImagePathsDto.builder()
                            .url(domain + path)
                            .igPath(path)
                            .build())
                    .collect(Collectors.toList());
        }

        return PlaceDto.builder()
                .cpIdx(this.cpIdx)
                .cpOrder(this.cpOrder)
                .cpType(this.cpType)
                .cpText(this.cpText)
                .pcIdx(this.pcIdx)
                .pcId(this.pcId)
                .pcLng(this.pcLng)
                .pcLat(this.pcLat)
                .pcName(this.pcName)
                .pcRating(this.pcRating)
                .images(images)
                .build();
    }
}
