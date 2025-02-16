package com.travelhub.travelhub_api.data.mysql.repository.tag;

import com.travelhub.travelhub_api.data.mysql.entity.TagEntity;
import com.travelhub.travelhub_api.data.mysql.repository.tag.custom.TagRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<TagEntity, Long>, TagRepositoryCustom {
}
