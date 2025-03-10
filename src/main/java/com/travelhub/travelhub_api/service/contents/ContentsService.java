package com.travelhub.travelhub_api.service.contents;

import com.travelhub.travelhub_api.common.component.clients.GoogleMapsClient;
import com.travelhub.travelhub_api.common.resource.exception.CustomException;
import com.travelhub.travelhub_api.controller.contents.request.ContentsRequest;
import com.travelhub.travelhub_api.controller.contents.response.ContentsListResponse;
import com.travelhub.travelhub_api.controller.contents.response.ContentsMainListResponse;
import com.travelhub.travelhub_api.controller.contents.response.ContentsResponse;
import com.travelhub.travelhub_api.data.dto.contents.*;
import com.travelhub.travelhub_api.data.dto.place.GooglePlaceDetailsDto;
import com.travelhub.travelhub_api.data.elastic.entity.TravelPlace;
import com.travelhub.travelhub_api.data.elastic.repository.TravelRepository;
import com.travelhub.travelhub_api.data.enums.ImageType;
import com.travelhub.travelhub_api.data.enums.common.ErrorCodes;
import com.travelhub.travelhub_api.data.mysql.entity.ContentsEntity;
import com.travelhub.travelhub_api.data.mysql.entity.ContentsPlaceEntity;
import com.travelhub.travelhub_api.data.mysql.entity.ContentsTagEntity;
import com.travelhub.travelhub_api.data.mysql.entity.PlaceEntity;
import com.travelhub.travelhub_api.data.mysql.repository.CityRepository;
import com.travelhub.travelhub_api.data.mysql.repository.ImageRepository;
import com.travelhub.travelhub_api.data.mysql.repository.PlaceRepository;
import com.travelhub.travelhub_api.data.mysql.repository.contents.ContentsPlaceRepository;
import com.travelhub.travelhub_api.data.mysql.repository.contents.ContentsRepository;
import com.travelhub.travelhub_api.data.mysql.repository.contents.ContentsTagRepository;
import com.travelhub.travelhub_api.data.mysql.repository.tag.TagRepository;
import com.travelhub.travelhub_api.data.mysql.support.ContentsRepositorySupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ContentsService {
    @Value("${google.api-key}")
    private String apiKey;
    private GoogleMapsClient mapsClient;
    private final TagRepository tagRepository;
    private final CityRepository cityRepository;
    private final ImageRepository imageRepository;
    private final PlaceRepository placeRepository;
    private final TravelRepository travelRepository;
    private final ContentsRepository contentsRepository;
    private final ContentsTagRepository contentsTagRepository;
    private final ContentsPlaceRepository contentsPlaceRepository;

    private final ContentsRepositorySupport contentsRepositorySupport;

    public void create(ContentsRequest request) {
        // contents 생성
        ContentsEntity contents = contentsRepository.save(request.ofContentsEntity());
        Long contentsIdx = contents.getCtIdx();

        // contents ⟷ tag 매핑
        contentsTagRepository.saveAll(request.ofContentsTagEntity(contentsIdx));

        // contents ⟷ places 매핑
        for (ContentsPlaceWriterDto contentsPlace : request.places()) {
            // 장소 저장
            Long placeIdx = upsertPlace(contentsPlace.pcId());

            // 이미지 저장
            Long imageIdx = imageRepository.save(contentsPlace.ofImageEntity(contentsIdx)).getIdx();

            contentsPlaceRepository.save(contentsPlace.ofContentsPlaceEntity(contentsIdx, placeIdx, imageIdx));
        }
    }

    public ContentsResponse get(Long contentsId) {
        // content with place 조회
        List<ContentsPlaceReaderDto> contentsPlaceList = contentsRepository.findContentsWithPlacesByContentsIdx(contentsId);
        if (contentsPlaceList.isEmpty()) {
            throw new CustomException(ErrorCodes.CONTENTS_NOT_FOUND);
        }

        // content, place 분기 처리
        ContentsDto contents = contentsPlaceList.get(0).ofContents();
        List<PlaceDto> places = contentsPlaceList.stream().map(ContentsPlaceReaderDto::ofPlace).collect(Collectors.toList());

        // tag 목록 조회
        List<TagDto> tags = tagRepository.findTagsByContentsIdx(contentsId);

        return ContentsResponse.builder()
                .contents(contents)
                .tags(tags)
                .places(places)
                .build();
    }

    public void update(Long contentsId, ContentsRequest request) {
        ContentsResponse contentSummary = get(contentsId);

        // 제목 업데이트
        updateContents(contentsId, request.title());

        // 태그 업데이트
        updateTag(contentsId, contentSummary.tags().stream().map(TagDto::tgIdx).toList(), request.tags());

        // 장소 업데이트
        updatePlace(contentsId, contentSummary.places(), request.places());

    }

    public void delete(Long contentsId) {
        // delete `contents`
        contentsRepository.deleteByCtIdx(contentsId);
        // delete `contents_place`
        contentsPlaceRepository.deleteByCtIdx(contentsId);
        // delete 'contents_tag`
        contentsTagRepository.deleteByCtIdx(contentsId);
        // delete `image`
        imageRepository.deleteByIdxAndIgType(contentsId, ImageType.CT);
    }

    public Page<ContentsListResponse> getList(List<String> tags, List<String> cities, Pageable pageable) {
        // tags, cities 가 있는 경우에만 join 처리
        return contentsRepository.findContentsByAllByTagsAndCities(tags, cities, pageable);
    }

    private Long upsertPlace(String placeId) {
        PlaceEntity place;

        // 임시 저장 된 장소 조회 from elasticsearch
        TravelPlace placeInfo = travelRepository.findById(placeId)
                .orElseThrow(() -> new CustomException(ErrorCodes.PLACE_NOT_FOUND));

        // 도시명 조회 from google
        String cityName = getPlaceCity(placeInfo.getPcId());
        if (null == cityName) {
            throw new CustomException(ErrorCodes.PLACE_CITY_NOT_FOUND, placeInfo.getPcName());
        }

        // 도시 인덱스 조회 from db
        Long citIdx = cityRepository.findByCitName(cityName)
                .orElseThrow(() -> new CustomException(ErrorCodes.CITY_NOT_FOUND))
                .getCitIdx();

        place = placeInfo.ofPlaceEntity(citIdx);

        // 저장된 장소가 있는 경우 update
        placeRepository.findByPcId(placeId)
                .ifPresent(placeEntity -> place.addIdx(placeEntity.getPcIdx()));

        return placeRepository.save(place).getPcIdx();
    }

    private String getPlaceCity(String placeId){
        String city = null;

        GooglePlaceDetailsDto placesDetail = mapsClient.getPlacesDetail(placeId, "address_component", "ko", apiKey);

        if (placesDetail != null && placesDetail.getResults() != null) {
            for (GooglePlaceDetailsDto.Result result : placesDetail.getResults()) {
                if (result.getAddressComponents() != null) {
                    // address_components 리스트를 순회
                    for (GooglePlaceDetailsDto.AddressComponent component : result.getAddressComponents()) {
                        // types에 "locality"가 포함되어 있는지 확인
                        if (component.getTypes() != null && component.getTypes().contains("locality")) {
                            // short_name 값을 city에 저장
                            city = component.getShortName();
                            break;
                        }
                    }
                }
                if (city != null) break;
            }
        }

        return city;
    }

    private void updateTag(Long contentIdx, List<Long> currTags, List<Long> reqTags) {
        List<ContentsTagEntity> saveList = new ArrayList<>();
        List<ContentsTagEntity> deleteList = new ArrayList<>();

        // 추가
        for (Long idx : reqTags) {
            if (!currTags.contains(idx)) {
                saveList.add(ContentsTagEntity.builder()
                        .ctIdx(contentIdx)
                        .tgIdx(idx)
                        .build());
            }
        }

        if (!saveList.isEmpty()) {
            contentsTagRepository.saveAll(saveList);
        }

        // 삭제
        for (Long idx : currTags) {
            if (!reqTags.contains(idx)) {
                deleteList.add(ContentsTagEntity.builder()
                        .ctIdx(contentIdx)
                        .tgIdx(idx)
                        .build());
            }
        }

        if (!deleteList.isEmpty()) {
            contentsTagRepository.deleteAll(deleteList);
        }
    }

    private void updatePlace(Long ctIdx, List<PlaceDto> currPlaces, List<ContentsPlaceWriterDto> reqPlaces) {
        List<ContentsPlaceEntity> saveList = new ArrayList<>();
        List<ContentsPlaceEntity> updateList = new ArrayList<>();
        List<ContentsPlaceEntity> dlelteList = new ArrayList<>();

        // 추가
        // 변경
        // 삭제

    }

    private void updateContents(Long ctIdx, String title) {
        ContentsEntity contents = ContentsEntity.builder()
                .ctIdx(ctIdx)
                .ctTitle(title)
                .build();

        contentsRepository.save(contents);
    }

    @Transactional(readOnly = true)
    public List<ContentsMainListResponse> findMainList(List<Long> tags, Pageable pageable) {
        List<ContentsMainListResponse> responses = new ArrayList<>();
        // 메인 컨텐츠 조회
        List<ContentsTagDTO> mainContents = contentsRepositorySupport.findMainContents(tags, pageable);

        // 태그별 그룹화
        Map<String, List<ContentsTagDTO>> groupByTag = mainContents.stream()
                .collect(Collectors.groupingBy(ContentsTagDTO::tagName));

        for (String tag : groupByTag.keySet()) {
            List<ContentsTagDTO> tagContents = groupByTag.getOrDefault(tag, new ArrayList<>());

            ContentsMainListResponse response = ContentsMainListResponse.builder()
                    .tgIdx(tagContents.get(0).tgIdx())
                    .tgName(tag)
                    .contents(ContentsListResponse.ofList(tagContents))
                    .build();

            responses.add(response);
        }
        return responses;
    }
}
