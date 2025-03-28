package com.travelhub.travelhub_api.data.dto.contents;

import com.travelhub.travelhub_api.data.enums.ContentsPlaceType;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public record ContentsPlaceReaderDto(
        Long ctIdx,
        String ctTitle,
        Double ctScore,
        LocalDateTime insertTime,
        LocalDateTime updateTime,
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
    public ContentsDto ofContents() {
        return ContentsDto.builder()
                .ctIdx(this.ctIdx)
                .ctTitle(this.ctTitle)
                .ctScore(this.ctScore)
                .insertTime(this.insertTime)
                .updateTime(this.updateTime)
                .build();
    }

    public PlaceDto ofPlace(String domain) {
        List<String> images = Arrays.stream(this.igPath.split(","))
                .map(path -> domain + path)
                .toList();

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
                .igPath(images)
                .build();
    }
}
