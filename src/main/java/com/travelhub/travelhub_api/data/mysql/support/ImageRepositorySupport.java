package com.travelhub.travelhub_api.data.mysql.support;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelhub.travelhub_api.data.dto.auth.LoginUserDTO;
import com.travelhub.travelhub_api.data.dto.storage.StorageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.travelhub.travelhub_api.data.mysql.entity.QImageEntity.imageEntity;
import static com.travelhub.travelhub_api.data.mysql.entity.QStorageEntity.storageEntity;

@Component
@RequiredArgsConstructor
public class ImageRepositorySupport {

    private final JPAQueryFactory queryFactory;

    public void findImage(Long igIdx) {
        String usId = LoginUserDTO.get();

        JPAQuery<StorageDTO> storageQuery = queryFactory.select(
                        Projections.fields(
                                StorageDTO.class,
                                storageEntity.stName,
                                storageEntity.stRegion,
                                storageEntity.stNamespace
                        )
                )
                .from(imageEntity)
                .innerJoin(storageEntity)
                .on(imageEntity.stIdx.eq(storageEntity.stIdx));

    }
}
