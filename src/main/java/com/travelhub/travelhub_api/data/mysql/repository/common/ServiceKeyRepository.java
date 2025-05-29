package com.travelhub.travelhub_api.data.mysql.repository.common;

import com.travelhub.travelhub_api.data.mysql.entity.common.ServiceKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceKeyRepository extends JpaRepository<ServiceKeyEntity, Long> {
    ServiceKeyEntity findTopBy();
}
