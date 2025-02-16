package com.travelhub.travelhub_api.data.mysql.repository.contents.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelhub.travelhub_api.controller.contents.response.ContentsListResponse;
import com.travelhub.travelhub_api.data.dto.contents.ContentsPlaceReaderDto;
import com.travelhub.travelhub_api.data.mysql.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class ContentsRepositoryImpl implements ContentsRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public List<ContentsPlaceReaderDto> findContentsWithPlacesByContentsIdx(Long ctIdx) {
        QImageEntity image = QImageEntity.imageEntity;
        QPlaceEntity place = QPlaceEntity.placeEntity;
        QContentsEntity contents = QContentsEntity.contentsEntity;
        QContentsPlaceEntity contentsPlace = QContentsPlaceEntity.contentsPlaceEntity;

        return query
                .select(Projections.constructor(ContentsPlaceReaderDto.class,
                        contents.ctIdx,
                        contents.ctTitle,
                        contents.ctScore,
                        contents.insertTime,
                        contents.updateTime,
                        contentsPlace.cpIdx,
                        contentsPlace.cpOrder,
                        contentsPlace.cpType,
                        contentsPlace.cpText,
                        place.pcIdx,
                        place.pcId,
                        place.pcLng,
                        place.pcLat,
                        place.pcName,
                        place.pcRating,
                        image.igPath
                ))
                .from(contents)
                .leftJoin(contentsPlace).on(contents.ctIdx.eq(contentsPlace.ctIdx))
                .leftJoin(place).on(contentsPlace.pcIdx.eq(place.pcIdx))
                .leftJoin(image).on(contentsPlace.igIdx.eq(image.igIdx))
                .where(contents.ctIdx.eq(ctIdx))
                .fetch();
    }

    @Override
    public Page<ContentsListResponse> findContentsByAllByTagsAndCities(List<String> tags, List<String> cities, Pageable pageable) {
        QTagEntity tag = QTagEntity.tagEntity;
        QCityEntity city = QCityEntity.cityEntity;
        QPlaceEntity place = QPlaceEntity.placeEntity;
        QContentsEntity contents = QContentsEntity.contentsEntity;
        QContentsTagEntity contentsTag = QContentsTagEntity.contentsTagEntity;
        QContentsPlaceEntity contentsPlace = QContentsPlaceEntity.contentsPlaceEntity;

        // 기본 쿼리 작성
        JPAQuery<ContentsListResponse> queryBuilder = query
                .select(Projections.constructor(ContentsListResponse.class, contents))
                .from(contents);

        // cities 가 비어있지 않을 경우에만 관련 테이블 조인 및 조건 추가
        if (cities != null && !cities.isEmpty()) {
            queryBuilder.join(contentsPlace).on(contents.ctIdx.eq(contentsPlace.ctIdx))
                    .join(place).on(contentsPlace.pcIdx.eq(place.pcIdx))
                    .join(city).on(place.citIdx.eq(city.citIdx)).on(city.citName.in(cities));
        }

        // tags 가 비어있지 않을 경우에만 관련 테이블 조인 및 조건 추가
        if (tags != null && !tags.isEmpty()) {
            queryBuilder.join(contentsTag).on(contents.ctIdx.eq(contentsTag.ctIdx))
                    .join(tag).on(contentsTag.ctgIdx.eq(tag.tgIdx)).on(tag.tgName.in(tags));
        }

        // 데이터 조회
        List<ContentsListResponse> contentList = queryBuilder.fetch();

        // 전체 데이터 개수 조회 (카운트 쿼리)
        JPAQuery<Long> countQuery = query.select(contents.count())
                .from(contents);

        if (cities != null && !cities.isEmpty()) {
            countQuery.join(contentsPlace).on(contents.ctIdx.eq(contentsPlace.ctIdx))
                    .join(place).on(contentsPlace.pcIdx.eq(place.pcIdx))
                    .join(city).on(place.citIdx.eq(city.citIdx)).on(city.citName.in(cities));
        }

        if (tags != null && !tags.isEmpty()) {
            countQuery.join(contentsTag).on(contents.ctIdx.eq(contentsTag.ctIdx))
                    .join(tag).on(contentsTag.ctgIdx.eq(tag.tgIdx)).on(tag.tgName.in(tags));
        }

        Long totalCount = countQuery.fetchOne(); // 전체 데이터 개수

        // Page 객체 생성 및 반환
        return new PageImpl<>(contentList, pageable, totalCount == null ? 0 : totalCount);
    }

}
