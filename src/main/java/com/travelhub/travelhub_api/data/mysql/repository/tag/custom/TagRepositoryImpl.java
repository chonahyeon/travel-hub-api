package com.travelhub.travelhub_api.data.mysql.repository.tag.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelhub.travelhub_api.data.dto.contents.TagDto;

import com.travelhub.travelhub_api.data.mysql.entity.QContentsTagEntity;
import com.travelhub.travelhub_api.data.mysql.entity.QTagEntity;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class TagRepositoryImpl implements TagRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public List<TagDto> findTagsByContentsIdx(Long ctIdx) {
        QContentsTagEntity contentsTag = QContentsTagEntity.contentsTagEntity;
        QTagEntity tag = QTagEntity.tagEntity;

        return query
                .select(Projections.constructor(TagDto.class,
                        tag.tgIdx,
                        tag.tgName
                ))
                .from(contentsTag)
                .join(tag).on(contentsTag.tgIdx.eq(tag.tgIdx))
                .where(contentsTag.ctIdx.eq(ctIdx))
                .fetch();
    }
}
