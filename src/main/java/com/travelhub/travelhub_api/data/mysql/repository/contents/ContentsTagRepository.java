package com.travelhub.travelhub_api.data.mysql.repository.contents;

import com.travelhub.travelhub_api.data.mysql.entity.ContentsTagEntity;
import com.travelhub.travelhub_api.data.mysql.repository.contents.custom.ContentsTagRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentsTagRepository extends JpaRepository<ContentsTagEntity, Long>, ContentsTagRepositoryCustom {
    void deleteByCtIdx(Long ctIdx);
}
