package com.travelhub.travelhub_api.controller.contents;

import com.travelhub.travelhub_api.controller.common.response.ApiResponse;
import com.travelhub.travelhub_api.controller.contents.request.ContentsRequest;
import com.travelhub.travelhub_api.controller.contents.response.ContentsCreateResponse;
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
    public ResponseEntity<Object> create(@Valid @RequestBody ContentsRequest request) {
        return ResponseEntity.ok(ApiResponse.success(contentsService.create(request)));
    }

    @GetMapping("/{contentsId}")
    public ResponseEntity<Object> get(@PathVariable Long contentsId) {
        return ResponseEntity.ok(ApiResponse.success(contentsService.get(contentsId)));
    }

    @PatchMapping("/{contentsId}")
    public ResponseEntity<Object> update(@PathVariable Long contentsId,
                                         @Valid @RequestBody ContentsRequest request) {
        contentsService.update(contentsId, request);

        return ResponseEntity.ok(ApiResponse.success());
    }

    @DeleteMapping("/{contentsId}")
    public ResponseEntity<Object> delete(@PathVariable Long contentsId) {
        contentsService.delete(contentsId);

        return ResponseEntity.ok(ApiResponse.success());
    }

    @GetMapping(LIST)
    public ResponseEntity<Object> getList(@RequestParam(required = false) List<String> tags,
                                          @RequestParam(required = false) String city,
                                          Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(contentsService.getList(tags, city, pageable)));
    }

    /**
     * 메인 여행코스 목록 조회 (태그별 집계)
     * GET /travel/v1/contents/main?tags=1,2&page=1&size=10
     */
    @GetMapping(MAIN)
    public ResponseEntity<Object> getMainList(@RequestParam(required = false) List<Long> tags,
                                              Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(contentsService.findMainList(tags, pageable)));
    }
}
