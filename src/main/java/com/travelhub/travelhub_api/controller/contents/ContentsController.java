package com.travelhub.travelhub_api.controller.contents;

import com.travelhub.travelhub_api.controller.contents.request.ContentsRequest;
import com.travelhub.travelhub_api.controller.contents.response.ContentsListResponse;
import com.travelhub.travelhub_api.controller.contents.response.ContentsMainListResponse;
import com.travelhub.travelhub_api.controller.contents.response.ContentsResponse;
import com.travelhub.travelhub_api.service.contents.ContentsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.travelhub.travelhub_api.common.resource.TravelHubResource.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_V1_CONTENTS)
public class ContentsController {

    private final ContentsService contentsService;

    @PostMapping
    public void create(@Valid @RequestBody ContentsRequest request) {
        contentsService.create(request);
    }

    @GetMapping("/{contentsId}")
    public ResponseEntity<ContentsResponse> get(@PathVariable Long contentsId) {
        return ResponseEntity.ok(contentsService.get(contentsId));
    }

    @PatchMapping("/{contentsId}")
    public void update(@PathVariable Long contentsId, @Valid @RequestBody ContentsRequest request) {
        contentsService.update(contentsId, request);
    }

    @DeleteMapping("/{contentsId}")
    public void delete(@PathVariable Long contentsId) {
        contentsService.delete(contentsId);
    }

    @GetMapping(LIST)
    public List<ContentsListResponse> getList(@RequestParam List<String> tags, @RequestParam String city, Pageable pageable) {
        return contentsService.getList(tags, city, pageable);
    }

    /**
     * 메인 여행코스 목록 조회 (태그별 집계)
     * GET /travel/v1/contents/main?tags=1,2&page=1&size=10
     */
    @GetMapping(MAIN)
    public List<ContentsMainListResponse> getMainList(@RequestParam List<Long> tags, Pageable pageable) {
        return contentsService.findMainList(tags, pageable);
    }
}
