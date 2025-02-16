package com.travelhub.travelhub_api.data.mysql.support;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelhub.travelhub_api.data.dto.image.MainPlaceListDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.travelhub.travelhub_api.data.mysql.entity.QCityEntity.cityEntity;
import static com.travelhub.travelhub_api.data.mysql.entity.QCountryEntity.countryEntity;
import static com.travelhub.travelhub_api.data.mysql.entity.QImageEntity.imageEntity;
import static com.travelhub.travelhub_api.data.mysql.entity.QStorageEntity.storageEntity;

@Component
@RequiredArgsConstructor
public class PlaceRepositorySuppport {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * main 이미지 조회용 쿼리
     * @return 메인 이미지 목록
     */
    public List<MainPlaceListDTO> findMainPlaces() {
        return jpaQueryFactory.select(
                        Projections.fields(
                                MainPlaceListDTO.class,
                                imageEntity.idx,
                                imageEntity.igIdx,
                                imageEntity.igPath,
                                imageEntity.igType,
                                storageEntity.stIdx,
                                storageEntity.stName,
                                storageEntity.stNamespace,
                                cityEntity.citIdx,
                                cityEntity.citName,
                                cityEntity.citDescription,
                                countryEntity.cntIdx,
                                countryEntity.cntName,
                                countryEntity.cntCode
                        )
                ).from(imageEntity)
                .innerJoin(cityEntity)
                .on(imageEntity.idx.eq(cityEntity.citIdx))
                .innerJoin(countryEntity)
                .on(cityEntity.cntIdx.eq(countryEntity.cntIdx))
                .innerJoin(storageEntity)
                .on(imageEntity.stIdx.eq(storageEntity.stIdx))
                .fetch();
    }
}
