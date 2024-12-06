package com.travelhub.travelhub_api.data.mysql.repository;

import com.travelhub.travelhub_api.data.mysql.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    void deleteByRvIdxAndUId(Long rvIdx, String uId);
}
