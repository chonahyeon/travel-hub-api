package com.travelhub.travelhub_api.data.dto.contents;

import java.time.LocalDateTime;

public record ContentsPlaceReaderDto(
        Long ctIdx,
        String ctTitle,
        Double ctScore,
        LocalDateTime insertTime,
        LocalDateTime updateTime,
        Long cpIdx,
        Integer cpOrder,
        String cpType,
        String cpText,
        Long pcIdx,
        String pcId,
        Double pcLng,
        Double pcLat,
        String pcName,
        Double pcRating,
        String igUrl
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

    public PlaceDto ofPlace() {
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
                .igUrl(this.igUrl)
                .build();
    }
}
