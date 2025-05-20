package com.travelhub.travelhub_api.data.mysql.repository.contents.custom;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelhub.travelhub_api.data.dto.contents.DeleteTagDto;
import com.travelhub.travelhub_api.data.mysql.entity.QContentsTagEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
public class ContentsTagRepositoryImpl implements ContentsTagRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public void deleteAllByTags(List<DeleteTagDto> tags) {
        QContentsTagEntity contentsTag = QContentsTagEntity.contentsTagEntity;

        BooleanExpression[] predicates = tags.stream()
                .map(tag -> contentsTag.ctIdx.eq(tag.ctIdx())
                        .and(contentsTag.tgIdx.eq(tag.tgIdx())))
                .toArray(BooleanExpression[]::new);

        query.delete(contentsTag)
                .where(ExpressionUtils.anyOf(predicates))
                .execute();
    }
}
