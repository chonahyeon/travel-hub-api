package com.travelhub.travelhub_api.service.place;

import com.travelhub.travelhub_api.common.component.clients.GoogleMapsClient;
import com.travelhub.travelhub_api.common.resource.exception.CustomException;
import com.travelhub.travelhub_api.controller.place.response.MainPlaceResponse;
import com.travelhub.travelhub_api.data.dto.image.MainPlaceListDTO;
import com.travelhub.travelhub_api.controller.place.response.PlaceResponse;
import com.travelhub.travelhub_api.data.dto.place.GooglePlacesDTO;
import com.travelhub.travelhub_api.data.elastic.entity.TravelPlace;
import com.travelhub.travelhub_api.data.elastic.repository.TravelRepository;
import com.travelhub.travelhub_api.data.enums.SearchType;
import com.travelhub.travelhub_api.data.enums.common.ErrorCodes;
import com.travelhub.travelhub_api.data.mysql.support.PlaceRepositorySuppport;
import com.travelhub.travelhub_api.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final PlaceRepositorySuppport placeRepositorySuppport;
    private final StorageService storageService;

    @Value("${google.api-key}")
    private String apiKey;

    public List<PlaceResponse> get(String name, String type, Pageable pageable) {
        List<PlaceResponse> response = new ArrayList<>();
        /*
         * searchType
         *  G = 일반 검색, R = 재검색
         */
        try {
            List<TravelPlace> places = null;

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

            if (!places.isEmpty()) {
                response = places.stream().map(TravelPlace::ofPlaceResponse).collect(Collectors.toList());
            }
        } catch (NoSuchElementException e) {
            log.warn("place not found. REQ = '{}'", name);
            throw new CustomException(ErrorCodes.INVALID_PARAM, name);
        } catch (Exception e) {
            log.error("장소 조회 error. ", e);
        }

        return response;
    }

    private List<TravelPlace> getGooglePlaces(String name){
        GooglePlacesDTO response = mapsClient.getPlaces(name, "ko", apiKey);

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

    @Transactional(readOnly = true)
    public List<MainPlaceResponse> findMainPlaces() {
        List<MainPlaceListDTO> mainPlaces = placeRepositorySuppport.findMainPlaces();
        String domain = storageService.getImageDomain();
        return MainPlaceResponse.ofListDTO(mainPlaces, domain);
    }

    private List<TravelPlace> paginateGooglePlaces(List<TravelPlace> places, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), places.size());

        if (start > places.size()) {
            return Collections.emptyList();
        }

        return places.subList(start, end);
    }
}
