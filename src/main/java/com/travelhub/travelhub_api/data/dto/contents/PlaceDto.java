package com.travelhub.travelhub_api.data.dto.contents;

import lombok.Builder;

@Builder
public record PlaceDto(
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
}
