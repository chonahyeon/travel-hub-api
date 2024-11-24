package com.travelhub.travelhub_api.service.place;

import com.travelhub.travelhub_api.common.component.FeignClient.GoogleMapsClient;
import com.travelhub.travelhub_api.data.dto.place.GooglePlacesResponse;
import com.travelhub.travelhub_api.data.elastic.repository.TravelRepository;
import com.travelhub.travelhub_api.data.elastic.entity.TravelPlace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceService {

    private final GoogleMapsClient mapsClient;
    private final TravelRepository travelRepository;

    @Value("${google.api-key}")
    private String apiKey;

    public Page<TravelPlace> get(String name, Pageable pageable) {
        Page<TravelPlace> places;

        // elasticSearch 조회
        places = travelRepository.findByPcNameContaining(name, pageable);

        if (places.isEmpty()) {
            // google place api 조회
            List<TravelPlace> googlePlaces = getGooglePlaces(name);
            travelRepository.saveAll(googlePlaces);

            // place api 응답 데이터 페이징 처리
            places = paginateGooglePlaces(googlePlaces, pageable);
        }

        return places;
    }

    private List<TravelPlace> getGooglePlaces(String name){
        GooglePlacesResponse response = mapsClient.getPlaces(name, apiKey);

        return response.getResults().stream().map(result -> TravelPlace.builder()
                .pcName(result.getName())
                .pcAddress(result.getFormattedAddress())
                .pcRating(result.getRating())
                .pcLng(result.getGeometry().getLocation().getLng())
                .pcLat(result.getGeometry().getLocation().getLat())
                .pcId(result.getPlaceId())
                .build()).collect(Collectors.toList());
    }

    private Page<TravelPlace> paginateGooglePlaces(List<TravelPlace> places, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), places.size());

        if (start > places.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, places.size());
        }

        return new PageImpl<>(places.subList(start, end), pageable, places.size());
    }
}
