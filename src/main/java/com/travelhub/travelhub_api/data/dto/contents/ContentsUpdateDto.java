package com.travelhub.travelhub_api.data.dto.contents;

import com.travelhub.travelhub_api.data.mysql.entity.ContentsEntity;
import lombok.Builder;

import java.util.List;

@Builder
public record ContentsUpdateDto (
        ContentsEntity contents,
        List<ContentsPlaceDto> contentsPlace,
        List<TagDto> tags
) {
}
