package com.travelhub.travelhub_api.data.mysql.repository.common;

import com.travelhub.travelhub_api.data.mysql.entity.common.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
