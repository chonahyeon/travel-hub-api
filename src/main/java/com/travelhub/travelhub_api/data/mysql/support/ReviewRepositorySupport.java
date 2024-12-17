package com.travelhub.travelhub_api.data.mysql.support;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelhub.travelhub_api.controller.review.response.ReviewListResponse;
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
        List<ReviewListResponse> reviews = queryFactory
                .select(
                        Projections.fields(
                                ReviewListResponse.class,
                                reviewEntity.rvIdx,
                                reviewEntity.rvScore,
                                reviewEntity.rvText,
                                reviewEntity.UId,
                                reviewEntity.ctIdx,
                                Expressions.stringTemplate("group_concat({0})", imageEntity.igUrl).as("images")
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
