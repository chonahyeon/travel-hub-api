package com.travelhub.travelhub_api.controller.review.request;

import lombok.NonNull;

public record ReviewImageRequest(
        Long igIdx,
        @NonNull
        String igUrl
) {
}
