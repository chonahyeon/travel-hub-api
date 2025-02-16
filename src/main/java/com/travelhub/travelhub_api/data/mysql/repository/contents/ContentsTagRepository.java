package com.travelhub.travelhub_api.data.mysql.repository.contents;

import com.travelhub.travelhub_api.data.mysql.entity.ContentsTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentsTagRepository extends JpaRepository<ContentsTagEntity, Long> {
    void deleteByCtIdx(Long ctIdx);
}
