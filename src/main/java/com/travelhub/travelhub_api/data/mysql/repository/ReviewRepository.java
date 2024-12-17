package com.travelhub.travelhub_api.data.mysql.repository;

import com.travelhub.travelhub_api.data.mysql.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findByCtIdx(Long ctIdx);
    Boolean existsByRvIdxAndUId(Long rvIdx, String uId);
    List<ReviewEntity> findByUId(String uId);
}
