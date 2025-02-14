package com.travelhub.travelhub_api.controller.review.response;

import lombok.Builder;

@Builder
public record ReviewCreateResponse(
        Long rvIdx
) {
}
