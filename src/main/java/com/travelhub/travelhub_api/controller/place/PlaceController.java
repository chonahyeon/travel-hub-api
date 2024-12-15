package com.travelhub.travelhub_api.controller.place;

import com.travelhub.travelhub_api.data.elastic.entity.TravelPlace;
import com.travelhub.travelhub_api.service.place.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.travelhub.travelhub_api.common.resource.TravelHubResource.API_V1_PLACES;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_V1_PLACES)
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<TravelPlace>>> get(@RequestParam String name,
                                                                    @RequestParam String type,
                                                                    Pageable pageable,
                                                                    PagedResourcesAssembler<TravelPlace> assembler) {
        return ResponseEntity.ok(assembler.toModel(placeService.get(name, type, pageable)));
    }

}
