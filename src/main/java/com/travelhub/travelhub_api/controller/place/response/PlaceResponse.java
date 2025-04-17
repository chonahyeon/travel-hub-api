package com.travelhub.travelhub_api.controller.place.response;

import lombok.Builder;

@Builder
public record PlaceResponse(
        String pcId,
        String pcName,
        String pcAddress,
        Double pcRating,
        String citName,
        double pcLng,
        double pcLat
) {
}
