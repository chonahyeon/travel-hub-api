package com.travelhub.travelhub_api.data.mysql.repository.contents;

import com.travelhub.travelhub_api.data.mysql.entity.ContentsPlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentsPlaceRepository extends JpaRepository<ContentsPlaceEntity, Long> {
    void deleteByCtIdx(Long ctIdx);

    void deleteByCpIdx(Long cpIdx);

    List<ContentsPlaceEntity> findAllByCtIdx(Long ctIdx);
}
