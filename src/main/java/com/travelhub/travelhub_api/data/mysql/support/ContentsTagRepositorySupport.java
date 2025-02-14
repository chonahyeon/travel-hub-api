package com.travelhub.travelhub_api.data.mysql.support;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelhub.travelhub_api.data.dto.tag.TagListDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.travelhub.travelhub_api.data.mysql.entity.QContentsTagEntity.contentsTagEntity;
import static com.travelhub.travelhub_api.data.mysql.entity.QTagEntity.tagEntity;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContentsTagRepositorySupport {

    private final JPAQueryFactory jpaQueryFactory;

    public List<TagListDTO> findTags(Pageable pageable) {
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
}
