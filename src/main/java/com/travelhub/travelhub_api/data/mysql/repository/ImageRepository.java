package com.travelhub.travelhub_api.data.mysql.repository;

import com.travelhub.travelhub_api.data.enums.ImageType;
import com.travelhub.travelhub_api.data.mysql.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
    void deleteByIdxAndIgType(Long idx, ImageType igType);
    List<ImageEntity> findByIgType(ImageType igType);
}
