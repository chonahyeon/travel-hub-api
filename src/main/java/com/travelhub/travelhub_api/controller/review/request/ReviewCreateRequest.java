package com.travelhub.travelhub_api.controller.review.request;

import com.travelhub.travelhub_api.data.dto.auth.LoginUserDTO;
import com.travelhub.travelhub_api.data.enums.ImageType;
import com.travelhub.travelhub_api.data.mysql.entity.ImageEntity;
import com.travelhub.travelhub_api.data.mysql.entity.ReviewEntity;
import lombok.NonNull;

import java.util.List;

public record ReviewCreateRequest(
        @NonNull
        String rvText,

        @NonNull
        Long ctIdx,

        @NonNull
        List<ReviewImageRequest> imageUrls
) {
    public ReviewEntity ofReview() {
        String uId = LoginUserDTO.get();

        return ReviewEntity.builder()
                .rvText(this.rvText)
                .UId(uId)
                .ctIdx(this.ctIdx)
                .build();
    }

    public ImageEntity ofImage(Long rvIdx, String url) {
        return ImageEntity.builder()
                .idx(rvIdx)
                .igType(ImageType.RV)
                .igUrl(url)
                .build();
    }
}
