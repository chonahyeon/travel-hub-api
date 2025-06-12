package com.travelhub.travelhub_api.service.contents;

import com.travelhub.travelhub_api.common.component.clients.GoogleMapsClient;
import com.travelhub.travelhub_api.common.resource.exception.CustomException;
import com.travelhub.travelhub_api.controller.common.response.ListResponse;
import com.travelhub.travelhub_api.controller.contents.request.ContentsRequest;
import com.travelhub.travelhub_api.controller.contents.response.ContentsCreateResponse;
import com.travelhub.travelhub_api.controller.contents.response.ContentsListResponse;
import com.travelhub.travelhub_api.controller.contents.response.ContentsMainListResponse;
import com.travelhub.travelhub_api.controller.contents.response.ContentsResponse;
import com.travelhub.travelhub_api.data.dto.contents.*;
import com.travelhub.travelhub_api.data.dto.place.GooglePlaceDetailsDto;
import com.travelhub.travelhub_api.data.elastic.entity.TravelPlace;
import com.travelhub.travelhub_api.data.elastic.repository.TravelRepository;
import com.travelhub.travelhub_api.data.enums.ImageType;
import com.travelhub.travelhub_api.data.enums.common.ResponseCodes;
import com.travelhub.travelhub_api.data.mysql.entity.*;
import com.travelhub.travelhub_api.data.mysql.repository.CityRepository;
import com.travelhub.travelhub_api.data.mysql.repository.ImageRepository;
import com.travelhub.travelhub_api.data.mysql.repository.PlaceRepository;
import com.travelhub.travelhub_api.data.mysql.repository.contents.ContentsPlaceRepository;
import com.travelhub.travelhub_api.data.mysql.repository.contents.ContentsRepository;
import com.travelhub.travelhub_api.data.mysql.repository.contents.ContentsTagRepository;
import com.travelhub.travelhub_api.data.mysql.repository.tag.TagRepository;
import com.travelhub.travelhub_api.data.mysql.support.ContentsRepositorySupport;
import com.travelhub.travelhub_api.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final GoogleMapsClient mapsClient;
    private final TagRepository tagRepository;
    private final CityRepository cityRepository;
    private final ImageRepository imageRepository;
    private final PlaceRepository placeRepository;
    private final TravelRepository travelRepository;
    private final ContentsRepository contentsRepository;
    private final ContentsTagRepository contentsTagRepository;
    private final ContentsPlaceRepository contentsPlaceRepository;

    private final ContentsRepositorySupport contentsRepositorySupport;

    private final StorageService storageService;

    public ContentsCreateResponse create(ContentsRequest request) {
        // contents 생성
        ContentsEntity contents = contentsRepository.save(request.ofContentsEntity());
        Long contentsIdx = contents.getCtIdx();

        // contents ⟷ tag 매핑
        contentsTagRepository.saveAll(request.ofContentsTagEntity(contentsIdx));

        // contents ⟷ places 매핑
        request.places().forEach(contentsPlace -> saveContentsPlace(contentsIdx, contentsPlace));

        return new ContentsCreateResponse(contentsIdx);
    }

    public ContentsResponse get(Long contentsId) {
        // content with place 조회
        List<ContentsPlaceReaderDto> contentsPlaceList = contentsRepository.findContentsWithPlacesByContentsIdx(contentsId);
        if (contentsPlaceList.isEmpty()) {
            throw new CustomException(ResponseCodes.CONTENTS_NOT_FOUND);
        }

        // content, place 분기 처리
        ContentsDto contents = contentsPlaceList.get(0).contents().ofContents();

        String domain = storageService.getImageDomain();
        List<PlaceDto> places = contentsPlaceList.stream()
                .map(contentsPlace -> contentsPlace.ofPlace(domain))
                .collect(Collectors.toList());

        // tag 목록 조회
        List<TagDto> tags = tagRepository.findTagsByContentsIdx(contentsId);

        return ContentsResponse.builder()
                .contents(contents)
                .tags(tags)
                .places(places)
                .build();
    }

    public void update(Long contentsId, ContentsRequest request) {
        ContentsUpdateDto contentsSummary = getContents(contentsId);

        // 제목 업데이트
        contentsSummary.contents().updateTitle(request.title());

        // 태그 업데이트
        updateTag(contentsId, contentsSummary.tags().stream().map(TagDto::tgIdx).toList(), request.tags());

        // 장소 업데이트
        updatePlace(contentsId, request.places(), contentsSummary);
    }

    public void delete(Long contentsId) {
        // delete `contents`
        contentsRepository.deleteByCtIdx(contentsId);
        // delete `contents_place`
        contentsPlaceRepository.deleteByCtIdx(contentsId);
        // delete 'contents_tag`
        contentsTagRepository.deleteByCtIdx(contentsId);
        // delete `image`
        contentsPlaceRepository.findAllByCtIdx(contentsId)
                .forEach(contentsPlace -> imageRepository.deleteByIdxAndIgType(contentsPlace.getCpIdx(), ImageType.CT));
    }

    public ListResponse<ContentsListResponse> getList(List<String> tags, String city, Pageable pageable) {
        // tags, cities 가 있는 경우에만 join 처리
        return new ListResponse<>(contentsRepository.findContentsByAllByTagsAndCities(tags, city, pageable));
    }

    private void saveContentsPlace(Long contentsIdx, ContentsPlaceWriterDto reqContentsPlace) {
        // 장소 조회 or 저장
        Long placeIdx = getPlace(reqContentsPlace.pcId());

        ContentsPlaceEntity contentsPlace = contentsPlaceRepository.save(reqContentsPlace.ofContentsPlaceEntity(contentsIdx, placeIdx));

        // 이미지 저장
        imageRepository.save(reqContentsPlace.ofImageEntity(contentsPlace.getCpIdx()));
    }

    private void updateContentsPlace(ContentsPlaceWriterDto reqContentsPlace, ContentsPlaceDto currContentsPlace) {
        // update `contents_place`
        currContentsPlace.contentsPlace().updatePlaceText(reqContentsPlace);
        // update `image`
        currContentsPlace.image().updateIgPath(reqContentsPlace.convertImages());
    }

    private Long getPlace(String placeId) {
        // 장소가 이미 저장 되어 있는 경우 skip
        return placeRepository.findByPcId(placeId)
                .map(PlaceEntity::getPcIdx)
                .orElseGet(() -> { // 장소 신규 등록
                    // 임시 저장 된 장소 조회 from elasticsearch
                    TravelPlace placeInfo = travelRepository.findById(placeId)
                            .orElseThrow(() -> new CustomException(ResponseCodes.PLACE_NOT_FOUND));

                    // 도시 인덱스 조회 from db
                    Long citIdx = cityRepository.findByCitName(placeInfo.getCitName())
                            .orElseThrow(() -> new CustomException(ResponseCodes.CITY_NOT_FOUND))
                            .getCitIdx();

                    PlaceEntity place = placeInfo.ofPlaceEntity(citIdx);

                    return placeRepository.save(place).getPcIdx();
                });
    }

    /*
     * 장소 상세 조회
     * = 도시명 조회 파싱으로 바꾸면서 안씀
     */
    private String getPlaceCity(String placeId){
        String city = null;

        GooglePlaceDetailsDto placesDetail = mapsClient.getPlacesDetail(placeId, "address_component", "ko", apiKey);

        if (placesDetail != null && placesDetail.getResults() != null) {
            city = placesDetail.getResults().stream()
                    .filter(result -> result.getAddressComponents() != null)
                    .flatMap(result -> result.getAddressComponents().stream())
                    .filter(component -> component.getTypes() != null && component.getTypes().contains("locality"))
                    .map(GooglePlaceDetailsDto.AddressComponent::getShortName)
                    .findFirst()
                    .orElse(null);
        }

        return city;
    }

    private void updateTag(Long contentIdx, List<Long> currTags, List<Long> reqTags) {
        // 추가
        List<ContentsTagEntity> saveList = reqTags.stream()
                .filter(idx -> !currTags.contains(idx))
                .map(idx -> ContentsTagEntity.builder()
                        .ctIdx(contentIdx)
                        .tgIdx(idx)
                        .build())
                .collect(Collectors.toList());

        if (!saveList.isEmpty()) {
            contentsTagRepository.saveAll(saveList);
        }

        // 삭제
        List<DeleteTagDto> deleteList = currTags.stream()
                .filter(idx -> !reqTags.contains(idx))
                .map(idx -> DeleteTagDto.builder()
                        .ctIdx(contentIdx)
                        .tgIdx(idx)
                        .build())
                .collect(Collectors.toList());

        if (!deleteList.isEmpty()) {
            contentsTagRepository.deleteAllByTags(deleteList);
        }
    }

    private void updatePlace(Long ctIdx, List<ContentsPlaceWriterDto> reqPlaces, ContentsUpdateDto contentsSummary) {
        Map<String, ContentsPlaceDto> currPlaces = contentsSummary.contentsPlace().stream()
                .collect(Collectors.toMap(
                        contentsPlace -> contentsPlace.place().getPcId(),
                        contentsPlace -> contentsPlace, (a, b) -> b)
                );
        List<String> reqPlaceIds = reqPlaces.stream().map(ContentsPlaceWriterDto::pcId).toList();

        for (ContentsPlaceWriterDto reqPlace : reqPlaces) {
            if (currPlaces.containsKey(reqPlace.pcId())) {
                // 변경
                updateContentsPlace(reqPlace, currPlaces.get(reqPlace.pcId()));
            } else {
                // 추가
                saveContentsPlace(ctIdx, reqPlace);
            }
        }

        // 삭제
        currPlaces.values().stream()
                .filter(contentsPlaceDto -> !reqPlaceIds.contains(contentsPlaceDto.place().getPcId()))
                .map(contentsPlaceDto -> contentsPlaceDto.contentsPlace().getCpIdx())
                .forEach(contentsPlaceIdx -> {
                    // delete `contents_place`
                    contentsPlaceRepository.deleteByCpIdx(contentsPlaceIdx);
                    // delete `image`
                    imageRepository.deleteByIdxAndIgType(contentsPlaceIdx, ImageType.CT);
        });
    }

    @Transactional(readOnly = true)
    public ListResponse<ContentsMainListResponse> findMainList(List<Long> tags, Pageable pageable) {
        List<ContentsMainListResponse> responses = new ArrayList<>();
        // 메인 컨텐츠 조회
        List<ContentsTagDTO> mainContents = contentsRepositorySupport.findMainContents(tags, pageable);

        // 태그별 그룹화
        Map<String, List<ContentsTagDTO>> groupByTag = mainContents.stream()
                .collect(Collectors.groupingBy(ContentsTagDTO::tgName));

        for (String tag : groupByTag.keySet()) {
            List<ContentsTagDTO> tagContents = groupByTag.getOrDefault(tag, new ArrayList<>());

            ContentsMainListResponse response = ContentsMainListResponse.builder()
                    .tgIdx(tagContents.get(0).tgIdx())
                    .tgName(tag)
                    .contents(ContentsListResponse.ofList(tagContents))
                    .build();

            responses.add(response);
        }
        return new ListResponse<>(responses);
    }

    private ContentsUpdateDto getContents(Long contentsId) {
        // content with place 조회
        List<ContentsSummaryDto> contentsSummary = contentsRepository.findContentsSummaryByContentsIdx(contentsId);
        if (contentsSummary.isEmpty()) {
            throw new CustomException(ResponseCodes.CONTENTS_NOT_FOUND);
        }

        List<ContentsPlaceDto> contentsPlaces = contentsSummary.stream()
                .map(ContentsSummaryDto::ofContentsPlace)
                .collect(Collectors.toList());

        return ContentsUpdateDto.builder()
                .contents(contentsSummary.get(0).contents())
                .contentsPlace(contentsPlaces)
                .tags(tagRepository.findTagsByContentsIdx(contentsId))
                .build();
    }
}
