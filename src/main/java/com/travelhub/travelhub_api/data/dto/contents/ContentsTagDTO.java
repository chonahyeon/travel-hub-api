package com.travelhub.travelhub_api.data.dto.contents;

public record ContentsTagDTO(
        Long ctIdx,
        String ctTitle,
        Double ctScore,
        String usId,
        String tagName,
        Long tgIdx
) {
}
