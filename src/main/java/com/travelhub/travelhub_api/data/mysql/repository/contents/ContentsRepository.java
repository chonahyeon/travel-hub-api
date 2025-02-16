package com.travelhub.travelhub_api.data.mysql.repository.contents;

import com.travelhub.travelhub_api.data.mysql.entity.ContentsEntity;
import com.travelhub.travelhub_api.data.mysql.repository.contents.custom.ContentsRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentsRepository extends JpaRepository<ContentsEntity, Long>, ContentsRepositoryCustom {
    void deleteByCtIdx(Long ctIdx);
}
