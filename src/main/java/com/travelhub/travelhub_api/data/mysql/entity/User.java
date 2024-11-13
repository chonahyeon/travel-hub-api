package com.travelhub.travelhub_api.data.mysql.entity;

import com.travelhub.travelhub_api.data.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "us_idx")
    private Long usIdx;

    @Column(name = "us_id", nullable = false)
    private String usId;

    @Column(name = "us_name", nullable = false)
    private String usName;

    @Column(name = "us_password", nullable = false)
    private String usPassword;

    @Column(name = "us_email", nullable = false)
    private String usEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "us_role", nullable = false)
    private Role usRole;

    @Column(name = "us_profile", nullable = false)
    private String us_profile;
}
