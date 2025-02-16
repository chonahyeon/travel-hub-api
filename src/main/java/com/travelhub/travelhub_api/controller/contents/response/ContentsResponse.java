package com.travelhub.travelhub_api.controller.contents.response;

import com.travelhub.travelhub_api.data.dto.contents.ContentsDto;
import com.travelhub.travelhub_api.data.dto.contents.PlaceDto;
import com.travelhub.travelhub_api.data.dto.contents.TagDto;
import lombok.Builder;

import java.util.List;

@Builder
public record ContentsResponse(
        ContentsDto contents,
        List<TagDto> tags,
        List<PlaceDto> places
) {
}
