package com.travelhub.travelhub_api.data.mysql.repository.tag.custom;

import com.travelhub.travelhub_api.data.dto.contents.TagDto;

import java.util.List;

public interface TagRepositoryCustom {
    List<TagDto> findTagsByContentsIdx(Long ctIdx);
}
