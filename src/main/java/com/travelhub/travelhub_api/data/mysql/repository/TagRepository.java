package com.travelhub.travelhub_api.data.mysql.repository;

import com.travelhub.travelhub_api.data.mysql.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<TagEntity, Long> {
}
