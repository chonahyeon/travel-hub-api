package com.travelhub.travelhub_api.data.mysql.support;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelhub.travelhub_api.data.dto.contents.ContentsTagDTO;
import com.travelhub.travelhub_api.data.dto.tag.TagListDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.travelhub.travelhub_api.data.mysql.entity.QContentsEntity.contentsEntity;
import static com.travelhub.travelhub_api.data.mysql.entity.QContentsTagEntity.contentsTagEntity;
import static com.travelhub.travelhub_api.data.mysql.entity.QTagEntity.tagEntity;

@Component
@RequiredArgsConstructor
public class ContentsRepositorySupport {

    private final JPAQueryFactory jpaQueryFactory;

    public List<TagListDTO> findContentsTag(Pageable pageable) {
        NumberPath<Long> counts = Expressions.numberPath(Long.class, "counts");

        JPAQuery<Long> subQueryCount = jpaQueryFactory.select(contentsTagEntity.count())
                .from(contentsTagEntity)
                .where(contentsTagEntity.tgIdx.eq(tagEntity.tgIdx));

        return jpaQueryFactory.select(
                        Projections.fields(
                                TagListDTO.class,
                                tagEntity.tgIdx,
                                tagEntity.tgName,
                                Expressions.asNumber(ExpressionUtils.as(subQueryCount, counts))
                        )
                ).from(tagEntity)
                .leftJoin(contentsTagEntity)
                .on(contentsTagEntity.tgIdx.eq(tagEntity.tgIdx))
                .offset(pageable.getPageNumber())
                .limit(pageable.getPageSize())
                .fetch();
    }

    public List<ContentsTagDTO> findMainContents(List<Long> tags, Pageable pageable) {
        BooleanBuilder condition = new BooleanBuilder();

        // 태그 정보가 있을 때
        if (tags != null && !tags.isEmpty()) {
            condition.and(contentsTagEntity.tgIdx.in(tags));
        }

        return jpaQueryFactory.select(
                Projections.fields(
                        ContentsTagDTO.class,
                        contentsEntity.ctIdx,
                        contentsEntity.ctTitle,
                        contentsEntity.ctScore,
                        contentsEntity.ctViewCount,
                        contentsEntity.usId
                )
        ).from(contentsTagEntity)
        .innerJoin(tagEntity)
            .on(contentsTagEntity.tgIdx.eq(tagEntity.tgIdx))
        .innerJoin(contentsEntity)
            .on(contentsTagEntity.ctIdx.eq(contentsEntity.ctIdx))
        .where(condition)
        .offset(pageable.getPageNumber())
        .limit(pageable.getPageSize())
        .orderBy(contentsEntity.insertTime.desc())
        .fetch();
    }
}
