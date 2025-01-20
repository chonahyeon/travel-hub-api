package com.travelhub.travelhub_api.data.mysql.repository;

import com.travelhub.travelhub_api.data.mysql.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findByCtIdx(Long ctIdx);
    List<ReviewEntity> findByUsId(String usId);
    Optional<ReviewEntity> findByUsIdAndRvIdx(String usId, Long rvIdx);
}
