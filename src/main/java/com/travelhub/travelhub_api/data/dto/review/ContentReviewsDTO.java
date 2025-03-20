package com.travelhub.travelhub_api.data.dto.review;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class ContentReviewsDTO {
    private Long rvIdx;
    private Double rvScore;
    private String rvText;
    private String usId;
    private Long ctIdx;
    private Long igIdx;
    private String igPath;
}
