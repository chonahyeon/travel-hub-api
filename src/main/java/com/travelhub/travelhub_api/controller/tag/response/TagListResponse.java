package com.travelhub.travelhub_api.controller.tag.response;

import com.travelhub.travelhub_api.data.dto.tag.TagListDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record TagListResponse(
        Long tgIdx,
        String tgName,
        Long count
) {

    public static List<TagListResponse> ofList(List<TagListDTO> tagListDTOS) {
        return tagListDTOS.stream()
                .map(TagListResponse::of)
                .toList();
    }

    public static TagListResponse of(TagListDTO tagListDTO) {
        return TagListResponse.builder()
                .tgIdx(tagListDTO.getTgIdx())
                .tgName(tagListDTO.getTgName())
                .count(tagListDTO.getCounts())
                .build();
    }
}
