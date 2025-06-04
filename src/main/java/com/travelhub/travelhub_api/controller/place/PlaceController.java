package com.travelhub.travelhub_api.controller.place;

import com.travelhub.travelhub_api.controller.common.response.ApiResponse;
import com.travelhub.travelhub_api.service.place.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.travelhub.travelhub_api.common.resource.TravelHubResource.API_V1_PLACES;
import static com.travelhub.travelhub_api.common.resource.TravelHubResource.MAIN;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_V1_PLACES)
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping
    public ResponseEntity<Object> get(@RequestParam String name,
                                      @RequestParam String type,
                                      @RequestParam String countryCode,
                                      Pageable pageable) {

        return ResponseEntity.ok(ApiResponse.success(placeService.get(name, type, countryCode, pageable)));
    }

    /**
     * 메인 장소 목록 조회
     * GET /travel/v1/places/main
     */
    @GetMapping(MAIN)
    public ResponseEntity<Object> findMainPlaces() {
        return ResponseEntity.ok(ApiResponse.success(placeService.findMainPlaces()));
    }
}
