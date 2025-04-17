package com.travelhub.travelhub_api.controller.place;

import com.travelhub.travelhub_api.controller.place.response.MainPlaceResponse;
import com.travelhub.travelhub_api.controller.place.response.PlaceResponse;
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

import java.util.List;

import static com.travelhub.travelhub_api.common.resource.TravelHubResource.API_V1_PLACES;
import static com.travelhub.travelhub_api.common.resource.TravelHubResource.MAIN;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_V1_PLACES)
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping
    public List<PlaceResponse> get(@RequestParam String name, @RequestParam String type, @RequestParam(name = "cnt_code") String cntCode, Pageable pageable) {
        return placeService.get(name, type, cntCode, pageable);
    }

    /**
     * 메인 장소 목록 조회
     * GET /travel/v1/places/main
     */
    @GetMapping(MAIN)
    public List<MainPlaceResponse> findMainPlaces() {
        return placeService.findMainPlaces();
    }
}
