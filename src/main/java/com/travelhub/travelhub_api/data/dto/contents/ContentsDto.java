package com.travelhub.travelhub_api.data.dto.contents;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ContentsDto(
        Long ctIdx,
        String ctTitle,
        Double ctScore,
        Long ctViewCount,
        LocalDateTime insertTime,
        LocalDateTime updateTime
) {
}
