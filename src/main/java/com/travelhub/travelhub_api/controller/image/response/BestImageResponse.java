package com.travelhub.travelhub_api.controller.image.response;

import com.travelhub.travelhub_api.data.dto.image.BestImageListDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record BestImageResponse(
        List<String> list
) {
    public static BestImageResponse of(List<BestImageListDTO> dto, String domain) {
        List<String> igPath = dto.stream()
                .map(bestImageListDTO -> bestImageListDTO.getIgPath().split(","))
                .filter(split -> split.length > 0)
                .map(splitImage -> domain + splitImage[0])
                .toList();

        return BestImageResponse.builder()
                .list(igPath)
                .build();
    }
}
