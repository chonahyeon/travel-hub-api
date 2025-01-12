package com.travelhub.travelhub_api.controller.review.request;

import com.travelhub.travelhub_api.data.dto.auth.LoginUserDTO;
import com.travelhub.travelhub_api.data.mysql.entity.ReviewEntity;
import lombok.NonNull;

public record ReviewCreateRequest(
        @NonNull
        String rvText,

        @NonNull
        Long ctIdx
) {
    public ReviewEntity ofEntity() {
        String usId = LoginUserDTO.get();

        return ReviewEntity.builder()
                .rvText(this.rvText)
                .usId(usId)
                .ctIdx(this.ctIdx)
                .build();
    }
}
