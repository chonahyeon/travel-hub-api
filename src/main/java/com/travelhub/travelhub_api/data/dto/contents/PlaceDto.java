package com.travelhub.travelhub_api.data.dto.contents;

import com.travelhub.travelhub_api.data.enums.ContentsPlaceType;
import lombok.Builder;

import java.util.List;

@Builder
public record PlaceDto(
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
        List<String> igPath
) {
}
