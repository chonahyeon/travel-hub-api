package com.travelhub.travelhub_api.data.dto.image;

import lombok.Builder;

@Builder
public record ImagePathsDto(
        String url,
        String igPath
) {
}
