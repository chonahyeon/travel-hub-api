package com.travelhub.travelhub_api.data.dto.contents;

import lombok.Builder;

@Builder
public record DeleteTagDto(
        Long ctIdx,
        Long tgIdx
) {
}
