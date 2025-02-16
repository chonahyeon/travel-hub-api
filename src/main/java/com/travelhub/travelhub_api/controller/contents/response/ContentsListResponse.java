package com.travelhub.travelhub_api.controller.contents.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ContentsListResponse(
        Long ctIdx,
        String ctTitle,
        String usId,
        Double ctScore,
        LocalDateTime insertTime,
        LocalDateTime updateTime,
        Integer ctViewCount
) {
}
