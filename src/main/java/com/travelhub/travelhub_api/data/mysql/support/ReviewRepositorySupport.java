package com.travelhub.travelhub_api.data.mysql.support;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelhub.travelhub_api.controller.review.response.ReviewListResponse;
import com.travelhub.travelhub_api.data.dto.review.ReviewDTO;
import com.travelhub.travelhub_api.data.enums.ImageType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.travelhub.travelhub_api.data.mysql.entity.QImageEntity.imageEntity;
import static com.travelhub.travelhub_api.data.mysql.entity.QReviewEntity.reviewEntity;

@Component
@RequiredArgsConstructor
public class ReviewRepositorySupport {

    private final JPAQueryFactory queryFactory;

    public void findReviews(Long ctIdx) {
        // todo : 수정
        List<ReviewDTO> reviews = queryFactory
                .select(
                        Projections.fields(
                                ReviewDTO.class,
                                reviewEntity.rvIdx,
                                reviewEntity.rvScore,
                                reviewEntity.rvText,
                                reviewEntity.usId,
                                reviewEntity.ctIdx,
                                Expressions.stringTemplate("group_concat({0})", imageEntity.igPath).as("rawImages")
                        )
                )
                .from(reviewEntity)
                .innerJoin(imageEntity)
                .on(
                        reviewEntity.rvIdx.eq(imageEntity.idx),
                        imageEntity.igType.eq(ImageType.RV)
                )
                .where(
                        reviewEntity.ctIdx.eq(ctIdx)
                )
                .groupBy(reviewEntity.rvIdx)
                .fetch();

    }


}
