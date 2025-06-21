package com.travelhub.travelhub_api.data.mysql.repository.contents.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelhub.travelhub_api.controller.contents.response.ContentsListResponse;
import com.travelhub.travelhub_api.data.dto.contents.ContentsPlaceReaderDto;
import com.travelhub.travelhub_api.data.dto.contents.ContentsSummaryDto;
import com.travelhub.travelhub_api.data.enums.ImageType;
import com.travelhub.travelhub_api.data.mysql.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.travelhub.travelhub_api.data.mysql.entity.QImageEntity.imageEntity;

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
                        contents,
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
                .leftJoin(image).on(contentsPlace.cpIdx.eq(image.idx)).on(imageEntity.igType.eq(ImageType.CT))
                .where(contents.ctIdx.eq(ctIdx))
                .fetch();
    }

    @Override
    public List<ContentsListResponse> findContentsByAllByTagsAndCities(List<String> tags, String cityName, Pageable pageable) {
        QTagEntity tag = QTagEntity.tagEntity;
        QCityEntity city = QCityEntity.cityEntity;
        QPlaceEntity place = QPlaceEntity.placeEntity;
        QContentsEntity contents = QContentsEntity.contentsEntity;
        QContentsTagEntity contentsTag = QContentsTagEntity.contentsTagEntity;
        QContentsPlaceEntity contentsPlace = QContentsPlaceEntity.contentsPlaceEntity;

        JPAQuery<ContentsListResponse> queryBuilder = query
                .selectDistinct(Projections.constructor(ContentsListResponse.class,
                        contents.ctIdx,
                        contents.ctTitle,
                        contents.ctScore,
                        contents.ctViewCount,
                        contents.usId,
                        contents.insertTime,
                        contents.updateTime))
                .from(contents);

        if (cityName != null && !cityName.isBlank()) {
            queryBuilder.join(contentsPlace).on(contents.ctIdx.eq(contentsPlace.ctIdx))
                    .join(place).on(contentsPlace.pcIdx.eq(place.pcIdx))
                    .join(city).on(place.citIdx.eq(city.citIdx)).on(city.citName.eq(cityName));
        }

        if (tags != null && !tags.isEmpty()) {
            queryBuilder.join(contentsTag).on(contents.ctIdx.eq(contentsTag.ctIdx))
                    .join(tag).on(contentsTag.tgIdx.eq(tag.tgIdx)).on(tag.tgName.in(tags));
            queryBuilder.groupBy(contentsTag.ctgIdx, tag.tgIdx);;
        }

        queryBuilder.offset(pageable.getPageNumber());
        queryBuilder.limit(pageable.getPageSize());

        return queryBuilder.fetch();
    }

    @Override
    public List<ContentsSummaryDto> findContentsSummaryByContentsIdx(Long ctIdx) {
        QImageEntity image = QImageEntity.imageEntity;
        QPlaceEntity place = QPlaceEntity.placeEntity;
        QContentsEntity contents = QContentsEntity.contentsEntity;
        QContentsPlaceEntity contentsPlace = QContentsPlaceEntity.contentsPlaceEntity;

        return query
                .select(Projections.constructor(ContentsSummaryDto.class,
                        contents,
                        contentsPlace,
                        place,
                        image
                ))
                .from(contents)
                .leftJoin(contentsPlace).on(contents.ctIdx.eq(contentsPlace.ctIdx))
                .leftJoin(place).on(contentsPlace.pcIdx.eq(place.pcIdx))
                .leftJoin(image).on(contentsPlace.cpIdx.eq(image.idx)).on(imageEntity.igType.eq(ImageType.CT))
                .where(contents.ctIdx.eq(ctIdx))
                .fetch();
    }

}
