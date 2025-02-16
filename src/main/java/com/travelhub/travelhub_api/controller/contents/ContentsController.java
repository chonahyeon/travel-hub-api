package com.travelhub.travelhub_api.controller.contents;

import com.travelhub.travelhub_api.controller.contents.request.ContentsRequest;
import com.travelhub.travelhub_api.controller.contents.response.ContentsListResponse;
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

import static com.travelhub.travelhub_api.common.resource.TravelHubResource.API_V1_CONTENTS;
import static com.travelhub.travelhub_api.common.resource.TravelHubResource.LIST;

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
    public ResponseEntity<PagedModel<EntityModel<ContentsListResponse>>> getList(@RequestParam List<String> tags,
                                                                                 @RequestParam List<String> cities,
                                                                                 Pageable pageable,
                                                                                 PagedResourcesAssembler<ContentsListResponse> assembler) {
        return ResponseEntity.ok(assembler.toModel(contentsService.getList(tags, cities, pageable)));
    }
}
