package com.travelhub.travelhub_api.data.mysql.repository.contents.custom;

import com.travelhub.travelhub_api.controller.contents.response.ContentsListResponse;
import com.travelhub.travelhub_api.data.dto.contents.ContentsPlaceReaderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ContentsRepositoryCustom {
    List<ContentsPlaceReaderDto> findContentsWithPlacesByContentsIdx(Long ctIdx);

    List<ContentsListResponse> findContentsByAllByTagsAndCities(List<String> tags, String city, Pageable pageable);

}
