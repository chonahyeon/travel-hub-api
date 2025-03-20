package com.travelhub.travelhub_api.controller.review.response;

import com.travelhub.travelhub_api.data.dto.review.ContentReviewsDTO;
import lombok.Builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Builder
public record ReviewListResponse(
        Long rvIdx,
        Double rvScore,
        String rvText,
        String usId,
        Long ctIdx,
        Long igIdx,
        List<String> igPath
) {

    public static List<ReviewListResponse> ofList(List<ContentReviewsDTO> reviews, String domain) {
        return reviews.stream()
                .map(review -> of(review, domain))
                .toList();
    }

    public static ReviewListResponse of(ContentReviewsDTO review, String domain) {
        List<String> domainPath = new ArrayList<>();
        // 이미지 경로가 있을때만 적용
        if (review.getIgPath() != null) {
            domainPath = Arrays.stream(review.getIgPath().split(","))
                    .map(path -> domain + path)
                    .toList();
        }
        return ReviewListResponse.builder()
                .rvIdx(review.getRvIdx())
                .ctIdx(review.getCtIdx())
                .rvScore(review.getRvScore())
                .rvText(review.getRvText())
                .usId(review.getUsId())
                .igIdx(review.getIgIdx())
                .igPath(domainPath)
                .build();
    }
}
