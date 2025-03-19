package com.travelhub.travelhub_api.data.mysql.support;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelhub.travelhub_api.data.dto.image.BestImageListDTO;
import com.travelhub.travelhub_api.data.enums.ContentsPlaceType;
import com.travelhub.travelhub_api.data.enums.ImageType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.travelhub.travelhub_api.data.mysql.entity.QCityEntity.cityEntity;
import static com.travelhub.travelhub_api.data.mysql.entity.QContentsEntity.contentsEntity;
import static com.travelhub.travelhub_api.data.mysql.entity.QContentsPlaceEntity.contentsPlaceEntity;
import static com.travelhub.travelhub_api.data.mysql.entity.QImageEntity.imageEntity;
import static com.travelhub.travelhub_api.data.mysql.entity.QPlaceEntity.placeEntity;

@Component
@RequiredArgsConstructor
public class ImageRepositorySupport {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 도시별 베스트 게시글 이미지
     * @param citIdx city idx
     */
    public List<BestImageListDTO> findContentBestImages(Long citIdx, Integer limit) {
        return jpaQueryFactory.select(
                Projections.fields(
                        BestImageListDTO.class,
                        contentsEntity.ctIdx,
                        imageEntity.igPath,
                        contentsEntity.ctScore
                )
        ).distinct()
        .from(cityEntity)
        .innerJoin(placeEntity)
            .on(cityEntity.citIdx.eq(placeEntity.citIdx))
        .innerJoin(contentsPlaceEntity)
            .on(contentsPlaceEntity.pcIdx.eq(placeEntity.pcIdx))
            .on(contentsPlaceEntity.cpType.eq(ContentsPlaceType.M))
            .on(contentsPlaceEntity.cpOrder.eq(1))
        .innerJoin(imageEntity)
            .on(contentsPlaceEntity.cpIdx.eq(imageEntity.idx)).on(imageEntity.igType.eq(ImageType.CT))
        .innerJoin(contentsEntity)
            .on(contentsPlaceEntity.ctIdx.eq(contentsEntity.ctIdx))
        .where(cityEntity.citIdx.eq(citIdx))
        .orderBy(contentsEntity.ctScore.desc())
        .limit(limit)
        .fetch();
    }
}
