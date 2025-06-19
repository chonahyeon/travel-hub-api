package com.travelhub.travelhub_api.service.place;

import com.travelhub.travelhub_api.common.component.clients.GoogleMapsClientV1;
import com.travelhub.travelhub_api.common.component.clients.GoogleMapsClientV2;
import com.travelhub.travelhub_api.common.resource.TravelHubResource;
import com.travelhub.travelhub_api.common.resource.exception.CustomException;
import com.travelhub.travelhub_api.controller.place.response.MainPlaceResponse;
import com.travelhub.travelhub_api.controller.place.response.PlaceResponse;
import com.travelhub.travelhub_api.data.dto.image.MainPlaceListDTO;
import com.travelhub.travelhub_api.data.dto.place.GooglePlaceRequestDTO;
import com.travelhub.travelhub_api.data.dto.place.GooglePlacesDTO;
import com.travelhub.travelhub_api.data.dto.place.GooglePlacesV2DTO;
import com.travelhub.travelhub_api.data.dto.place.GooglePlacesV2DTO.AddressComponents;
import com.travelhub.travelhub_api.data.elastic.entity.TravelPlace;
import com.travelhub.travelhub_api.data.elastic.repository.TravelRepository;
import com.travelhub.travelhub_api.data.enums.SearchType;
import com.travelhub.travelhub_api.data.enums.common.ResponseCodes;
import com.travelhub.travelhub_api.data.mysql.entity.CountryEntity;
import com.travelhub.travelhub_api.data.mysql.repository.CountryRepository;
import com.travelhub.travelhub_api.data.mysql.support.PlaceRepositorySuppport;
import com.travelhub.travelhub_api.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceService {

    private final GoogleMapsClientV1 mapsClient;
    private final GoogleMapsClientV2 mapsClientV2;
    private final TravelRepository travelRepository;
    private final PlaceRepositorySuppport placeRepositorySuppport;
    private final StorageService storageService;
    private final CountryRepository countryRepository;

    public List<PlaceResponse> get(String name, String type, String cntCode, Pageable pageable) {
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
                // 국가 정보 조회
                CountryEntity countryEntity = countryRepository.findByCntCode(cntCode)
                        .orElseThrow(() -> new CustomException(ResponseCodes.INVALID_PARAM, "countryCode"));

                // google place api 조회
                GooglePlaceRequestDTO requestDTO = GooglePlaceRequestDTO.of(name, countryEntity);
                log.info("> 구글맵 Request : {}", requestDTO);
                List<TravelPlace> googlePlaces = getGooglePlacesV2(requestDTO);

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
            throw new CustomException(ResponseCodes.INVALID_PARAM, name);
        } catch (Exception e) {
            log.error("장소 조회 error. ", e);
        }

        return response;
    }

    /*
     * 구글맵 검색 V1
     */
    private List<TravelPlace> getGooglePlaces(String name, CountryEntity countryEntity){
        GooglePlacesDTO response = mapsClient.getPlaces(name, "ko", TravelHubResource.googleMapKey, countryEntity.getCntCode());

        List<TravelPlace> places = response.getResults().stream()
                .map(result -> {
                    // 도시 정보 추가.
                    String compoundCode = (result.getPlusCode() != null ? result.getPlusCode().getCompoundCode() : null);
                    String citName = parseAddressToCity(compoundCode, result.getFormattedAddress(), countryEntity.getCntCode());

                    TravelPlace formattedPlace = null;
                    if (citName != null) {
                        formattedPlace =  TravelPlace.builder()
                                .pcName(result.getName())
                                .pcAddress(result.getFormattedAddress())
                                .pcRating(result.getRating())
                                .pcLng(result.getGeometry().getLocation().getLng())
                                .pcLat(result.getGeometry().getLocation().getLat())
                                .pcId(result.getPlaceId())
                                .compoundCode(result.getPlusCode() != null ? result.getPlusCode().getCompoundCode() : null)
                                .citName(citName)
                                .build();
                    }
                    return formattedPlace;
                })
                .filter(Objects::nonNull)
                .toList();

        if (places.isEmpty()) {
            throw new NoSuchElementException();
        }

        return places;
    }

    /**
     * 구글맵 검색 v2
     * @param requestDTO request body
     */
    private List<TravelPlace> getGooglePlacesV2(GooglePlaceRequestDTO requestDTO) {
        GooglePlacesV2DTO places = mapsClientV2.getPlaces(TravelHubResource.googleMapSearchFields, requestDTO);
        // formatting
        List<TravelPlace> travelPlaces = places.getPlaces()
                .stream()
                .map(place -> {
                    AddressComponents addressComponents = parseAddressToCity(place.getFormattedAddress(), place.getAddressComponents());
                    return (addressComponents != null ? place.of(addressComponents.getShortText(), addressComponents.getLanguageCode()) : null);
                })
                .filter(Objects::nonNull)
                .toList();
        if (travelPlaces.isEmpty()) throw new NoSuchElementException();
        return travelPlaces;
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

    private AddressComponents parseAddressToCity(String formattedAddress, List<AddressComponents> addressComponents) {
        return addressComponents.stream()
                .filter(components -> components.getTypes().contains("administrative_area_level_1"))
                .findFirst()
                .orElseGet(() -> {
                    log.warn("도시명 없음. 원본 주소 : {}", formattedAddress);
                    return null;
                });
    }

    /**
     * 주소에서 도시 파싱
     * @param compoundCode 구글맵 응답
     * @param formattedAddress 구글맵 응답
     * @param cntCode 국가 코드
     */
    private String parseAddressToCity(String compoundCode, String formattedAddress, String cntCode) {
        String cityName = "";
        switch (cntCode.toUpperCase()) {
            case "JP" -> {
                // compound code 정보가 있을 때
                if (compoundCode != null) {
                    String[] split = compoundCode.split(" ");
                    String rawCityName = split[split.length - 1];
                    /*
                     * 오사카부 -> 오사카
                     * 도쿄도 -> 도쿄
                     */
                    cityName = rawCityName.substring(0, rawCityName.length() - 1);
                } else {
                    // 도/부/현 추출용 정규표현식
                    Pattern pattern = Pattern.compile("(\\S+?[도부현])");
                    Matcher matcher = pattern.matcher(formattedAddress);
                    if (matcher.find()) {
                        cityName = matcher.group(1);
                    }
                }
            }
            case "KR" -> {
                String[] split = formattedAddress.split(" ");
                cityName = split[0];
            }
        }

        log.info("주소 : {} -> {}", formattedAddress, cityName);
        return cityName;
    }
}
