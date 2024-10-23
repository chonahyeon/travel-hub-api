package com.travelhub.travelhub_api.data.mysql.repository;

import com.travelhub.travelhub_api.data.mysql.entity.ContentsBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentsBookmarkRepository extends JpaRepository<ContentsBookmark, Long> {
}
