package com.travelhub.travelhub_api.controller.review.response;

import com.travelhub.travelhub_api.data.mysql.entity.ReviewEntity;
import lombok.Builder;

import java.util.List;

@Builder
public record ReviewListResponse(
        Long rvIdx,
        Double rvScore,
        String rvText,
        String uId,
        Long ctIdx,
        List<String> images
) {

    public static List<ReviewListResponse> ofList(List<ReviewEntity> reviews) {
        return reviews.stream()
                .map(ReviewListResponse::of)
                .toList();
    }

    public static ReviewListResponse of(ReviewEntity review) {
        return ReviewListResponse.builder()
                .ctIdx(review.getCtIdx())
                .rvIdx(review.getRvIdx())
                .rvScore(review.getRvScore())
                .rvText(review.getRvText())
                .uId(review.getUsId())
                .ctIdx(review.getCtIdx())
                .build();
    }
}
