package com.travelhub.travelhub_api.service.tag;

import com.travelhub.travelhub_api.controller.tag.response.TagListResponse;
import com.travelhub.travelhub_api.data.dto.tag.TagListDTO;
import com.travelhub.travelhub_api.data.mysql.repository.tag.TagRepository;
import com.travelhub.travelhub_api.data.mysql.support.ContentsTagRepositorySupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final ContentsTagRepositorySupport contentsTagRepositorySupport;

    /**
     * 태그 목록 조회
     * @param pageable 페이징
     */
    public List<TagListResponse> findTagList(Pageable pageable) {
        List<TagListDTO> tags = contentsTagRepositorySupport.findTags(pageable);
        return TagListResponse.ofList(tags);
    }
}
