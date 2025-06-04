package com.travelhub.travelhub_api.controller.tag;

import com.travelhub.travelhub_api.controller.common.response.ApiResponse;
import com.travelhub.travelhub_api.service.tag.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.travelhub.travelhub_api.common.resource.TravelHubResource.API_V1_TAGS;
import static com.travelhub.travelhub_api.common.resource.TravelHubResource.LIST;

@RestController
@RequestMapping(API_V1_TAGS)
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    /**
     * 태그 목록 조회
     * GET /travel/v1/tags/list?page=1&size=10
     */
    @GetMapping(LIST)
    public ResponseEntity<Object> findTagList(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(tagService.findTagList(pageable)));
    }
}
