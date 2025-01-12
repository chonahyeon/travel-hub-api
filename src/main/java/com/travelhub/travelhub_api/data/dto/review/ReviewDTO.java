package com.travelhub.travelhub_api.data.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@AllArgsConstructor
@Builder
public class ReviewDTO {
    private Long rvIdx;
    private Double rvScore;
    private String rvText;
    private String UId;
    private Long ctIdx;
    private String rawImages;
    private List<String> images;

    public void setRawImages(){

    }
}
