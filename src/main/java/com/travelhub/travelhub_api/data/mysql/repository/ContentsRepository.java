package com.travelhub.travelhub_api.data.mysql.repository;

import com.travelhub.travelhub_api.data.mysql.entity.ContentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentsRepository extends JpaRepository<ContentsEntity, Long> {
}
