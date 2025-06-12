package com.travelhub.travelhub_api.service.place;

import com.travelhub.travelhub_api.common.component.clients.GoogleMapsClient;
import com.travelhub.travelhub_api.common.resource.exception.CustomException;
import com.travelhub.travelhub_api.controller.common.response.ListResponse;
import com.travelhub.travelhub_api.controller.place.response.MainPlaceResponse;
import com.travelhub.travelhub_api.controller.place.response.ContentsPlaceResponse;
import com.travelhub.travelhub_api.data.dto.image.MainPlaceListDTO;
import com.travelhub.travelhub_api.data.dto.place.GooglePlacesDTO;
import com.travelhub.travelhub_api.data.dto.place.GooglePlacesDTO.PlaceResultDto;
import com.travelhub.travelhub_api.data.elastic.entity.TravelPlace;
import com.travelhub.travelhub_api.data.elastic.repository.TravelRepository;
import com.travelhub.travelhub_api.data.enums.SearchType;
import com.travelhub.travelhub_api.data.enums.common.ResponseCodes;
import com.travelhub.travelhub_api.data.mysql.entity.CountryEntity;
import com.travelhub.travelhub_api.data.mysql.repository.CityRepository;
import com.travelhub.travelhub_api.data.mysql.repository.CountryRepository;
import com.travelhub.travelhub_api.data.mysql.support.PlaceRepositorySuppport;
import com.travelhub.travelhub_api.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    private final GoogleMapsClient mapsClient;
    private final TravelRepository travelRepository;
    private final PlaceRepositorySuppport placeRepositorySuppport;
    private final StorageService storageService;
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final String localeCountryName = Locale.getDefault().getDisplayCountry(Locale.KOREAN);

    @Value("${google.api-key}")
    private String apiKey;

    public ListResponse<ContentsPlaceResponse> get(String name, String type, String cntCode, Pageable pageable) {
        List<ContentsPlaceResponse> response = new ArrayList<>();
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
                List<TravelPlace> googlePlaces = getGooglePlaces(name, countryEntity);

                // Region 설정해도 위치 기반으로 나오는 케이스 방지
                Locale defaultLocale = Locale.getDefault();
                String countryNameInKorean = defaultLocale.getDisplayCountry(Locale.KOREAN);

                googlePlaces = googlePlaces.stream()
                        .filter(info -> {
                            log.info("{}", !info.getPcAddress().contains(countryNameInKorean));
                            return !info.getPcAddress().contains(countryNameInKorean);
                        })
                        .toList();

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

        return new ListResponse<>(response);
    }

    private List<TravelPlace> getGooglePlaces(String name, CountryEntity countryEntity){
        GooglePlacesDTO response = mapsClient.getPlaces(name, "ko", apiKey, countryEntity.getCntCode());

        List<TravelPlace> places = response.getResults().stream()
                .map(result -> {
                    // 도시 정보 추가.
                    String citName = parseAddressToCity(result, countryEntity);
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

    @Transactional(readOnly = true)
    public ListResponse<MainPlaceResponse> findMainPlaces() {
        List<MainPlaceListDTO> mainPlaces = placeRepositorySuppport.findMainPlaces();
        String domain = storageService.getImageDomain();
        return new ListResponse<>(MainPlaceResponse.ofListDTO(mainPlaces, domain));
    }

    private List<TravelPlace> paginateGooglePlaces(List<TravelPlace> places, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), places.size());

        if (start > places.size()) {
            return Collections.emptyList();
        }

        return places.subList(start, end);
    }

    /**
     * 주소에서 도시 파싱
     * @param placeResultDto 조회 정보
     * @param countryEntity 국가 정보
     */
    private String parseAddressToCity(PlaceResultDto placeResultDto, CountryEntity countryEntity) {
        String compoundCode = (placeResultDto.getPlusCode() != null ? placeResultDto.getPlusCode().getCompoundCode() : null);
        String formattedAddress = placeResultDto.getFormattedAddress();
        String cityName = "";

        if (formattedAddress.contains(localeCountryName)) {
            log.info("로컬 리전 포함 Return 생략 : {}", formattedAddress);
            return null;
        }

        switch (countryEntity.getCntCode()) {
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
        };

        log.info("주소 : {} -> {}", formattedAddress, cityName);
        return cityName;
    }
}
