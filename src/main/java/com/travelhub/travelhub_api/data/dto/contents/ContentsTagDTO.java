package com.travelhub.travelhub_api.data.dto.contents;

import java.time.LocalDateTime;

public record ContentsTagDTO(
        Long ctIdx,
        String ctTitle,
        Double ctScore,
        Long ctViewCount,
        String usId,
        LocalDateTime insertTime,
        LocalDateTime updateTime,
        Long tgIdx,
        String tgName
) {
}
