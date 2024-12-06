package com.travelhub.travelhub_api.data.mysql.repository.common;

import com.travelhub.travelhub_api.data.mysql.entity.common.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {
}
