package com.travelhub.travelhub_api.service.place;

import com.travelhub.travelhub_api.common.component.FeignClient.GoogleMapsClient;
import com.travelhub.travelhub_api.common.resource.exception.CustomException;
import com.travelhub.travelhub_api.data.dto.place.GooglePlacesResponse;
import com.travelhub.travelhub_api.data.elastic.repository.TravelRepository;
import com.travelhub.travelhub_api.data.elastic.entity.TravelPlace;
import com.travelhub.travelhub_api.data.enums.SearchType;
import com.travelhub.travelhub_api.data.enums.common.ErrorCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceService {

    private final GoogleMapsClient mapsClient;
    private final TravelRepository travelRepository;

    @Value("${google.api-key}")
    private String apiKey;

    public Page<TravelPlace> get(String name, String type, Pageable pageable) {
        Page<TravelPlace> places = null;
        /*
         * searchType
         *  G = 일반 검색, R = 재검색
         */
        try {
            if (SearchType.G.toString().equalsIgnoreCase(type)) {
                // 일반 검색인 경우에만, elasticSearch 조회
                places = travelRepository.findByPcNameContaining(name, pageable);
            }

            if (null == places || places.isEmpty()) {
                // google place api 조회
                List<TravelPlace> googlePlaces = getGooglePlaces(name);
                // elasticSearch 저장
                travelRepository.saveAll(googlePlaces);

                // place api 응답 데이터 페이징 처리
                places = paginateGooglePlaces(googlePlaces, pageable);
            }
        } catch (NoSuchElementException e) {
            log.warn("place not found. REQ = '{}'", name);
            throw new CustomException(ErrorCodes.PLACE_NOT_FOUND);
        } catch (Exception e) {
            log.error("place search failed. ", e);
            throw new CustomException(ErrorCodes.SERVER_ERROR);
        }

        return places;
    }

    private List<TravelPlace> getGooglePlaces(String name){
        GooglePlacesResponse response = mapsClient.getPlaces(name, "ko", apiKey);

        List<TravelPlace> places = response.getResults().stream().map(result -> TravelPlace.builder()
                .pcName(result.getName())
                .pcAddress(result.getFormattedAddress())
                .pcRating(result.getRating())
                .pcLng(result.getGeometry().getLocation().getLng())
                .pcLat(result.getGeometry().getLocation().getLat())
                .pcId(result.getPlaceId())
                .build()).collect(Collectors.toList());

        if (places.isEmpty()) {
            throw new NoSuchElementException();
        }

        return places;
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
