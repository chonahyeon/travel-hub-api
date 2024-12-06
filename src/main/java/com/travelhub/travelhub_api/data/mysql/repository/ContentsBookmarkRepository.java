package com.travelhub.travelhub_api.data.mysql.repository;

import com.travelhub.travelhub_api.data.mysql.entity.ContentsBookmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentsBookmarkRepository extends JpaRepository<ContentsBookmarkEntity, Long> {
}
