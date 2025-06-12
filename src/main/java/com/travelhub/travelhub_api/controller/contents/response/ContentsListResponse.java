package com.travelhub.travelhub_api.controller.contents.response;

import com.travelhub.travelhub_api.data.dto.contents.ContentsTagDTO;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ContentsListResponse(
        Long ctIdx,
        String ctTitle,
        Double ctScore,
        Long ctViewCount,
        String usId,
        LocalDateTime insertTime,
        LocalDateTime updateTime
) {

    public static List<ContentsListResponse> ofList(List<ContentsTagDTO> contentsTagDTOS) {
        return contentsTagDTOS.stream()
                .map(ContentsListResponse::of)
                .toList();
    }

    public static ContentsListResponse of(ContentsTagDTO contentsTagDTO) {
        return ContentsListResponse.builder()
                .ctIdx(contentsTagDTO.ctIdx())
                .ctTitle(contentsTagDTO.ctTitle())
                .ctViewCount(contentsTagDTO.ctViewCount())
                .ctScore(contentsTagDTO.ctScore())
                .usId(contentsTagDTO.usId())
                .insertTime(contentsTagDTO.insertTime())
                .updateTime(contentsTagDTO.updateTime())
                .build();
    }
}
