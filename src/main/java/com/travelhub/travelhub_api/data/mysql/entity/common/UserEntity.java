package com.travelhub.travelhub_api.data.mysql.entity.common;

import com.travelhub.travelhub_api.controller.auth.request.SignUpRequest;
import com.travelhub.travelhub_api.data.enums.common.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserEntity {

    @Id
    @Column(name = "us_id")
    private String usId;

    @Column(name = "us_login_id")
    private String usLoginId;

    @Column(name = "us_pwd")
    private String usPwd;

    @Column(name = "us_nickname")
    private String usNickname;

    @Column(name = "us_name")
    private String usName;

    @Column(name = "us_email")
    private String usEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "us_role", nullable = false)
    private Role usRole;

    @Column(name = "us_profile", nullable = false)
    private String usProfile;

    public void updateSignUpInfo(SignUpRequest request) {
        this.usLoginId = request.id();
        this.usPwd = request.password();
        this.usNickname = request.nickName();
        this.usEmail = request.email();
        this.usRole = Role.ROLE_USER;
    }
}
