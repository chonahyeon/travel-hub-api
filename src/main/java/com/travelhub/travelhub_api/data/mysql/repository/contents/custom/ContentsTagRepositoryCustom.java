package com.travelhub.travelhub_api.data.mysql.repository.contents.custom;

import com.travelhub.travelhub_api.data.dto.contents.DeleteTagDto;

import java.util.List;

public interface ContentsTagRepositoryCustom {
    void deleteAllByTags(List<DeleteTagDto> tags);
}
