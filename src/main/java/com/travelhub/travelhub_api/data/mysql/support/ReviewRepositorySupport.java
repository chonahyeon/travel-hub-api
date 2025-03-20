package com.travelhub.travelhub_api.data.mysql.support;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelhub.travelhub_api.data.dto.review.ContentReviewsDTO;
import com.travelhub.travelhub_api.data.enums.ImageType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.travelhub.travelhub_api.data.mysql.entity.QImageEntity.imageEntity;
import static com.travelhub.travelhub_api.data.mysql.entity.QReviewEntity.reviewEntity;

@Component
@RequiredArgsConstructor
public class ReviewRepositorySupport {

    private final JPAQueryFactory queryFactory;

    /**
     * 리뷰 목록 조회
     */
    public List<ContentReviewsDTO> findReviews(Long ctIdx, Pageable pageable) {
        return queryFactory.select(
                Projections.fields(
                        ContentReviewsDTO.class,
                        reviewEntity.rvIdx,
                        reviewEntity.rvScore,
                        reviewEntity.rvText,
                        reviewEntity.usId,
                        reviewEntity.ctIdx,
                        imageEntity.igIdx,
                        imageEntity.igPath
                )
        ).from(reviewEntity)
        .leftJoin(imageEntity)
            .on(reviewEntity.rvIdx.eq(imageEntity.idx))
            .on(imageEntity.igType.eq(ImageType.RV))
        .where(reviewEntity.ctIdx.eq(ctIdx))
        .offset(pageable.getPageNumber())
        .limit(pageable.getPageSize())
        .fetch();
    }

    /**
     * 유저가 작성한 리뷰 목록 조회
     */
    public List<ContentReviewsDTO> findUserReviews(String usId, Pageable pageable) {
        return queryFactory.select(
                Projections.fields(
                        ContentReviewsDTO.class,
                        reviewEntity.rvIdx,
                        reviewEntity.rvScore,
                        reviewEntity.rvText,
                        reviewEntity.usId,
                        reviewEntity.ctIdx,
                        imageEntity.igIdx,
                        imageEntity.igType,
                        imageEntity.igPath
                )
        ).from(reviewEntity)
        .leftJoin(imageEntity)
            .on(reviewEntity.rvIdx.eq(imageEntity.idx))
            .on(imageEntity.igType.eq(ImageType.RV))
        .where(reviewEntity.usId.eq(usId))
        .offset(pageable.getPageNumber())
        .limit(pageable.getPageSize())
        .fetch();
    }
}
