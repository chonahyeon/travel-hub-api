package com.travelhub.travelhub_api.data.mysql.repository;

import com.travelhub.travelhub_api.data.mysql.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
