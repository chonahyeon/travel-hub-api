package com.travelhub.travelhub_api.data.mysql.entity;

import org.hibernate.annotations.DynamicInsert;

import com.travelhub.travelhub_api.data.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
public class User {

    @Id
    @Column(name = "us_id", nullable = false)
    private String usId;

    @Column(name = "us_name", nullable = false)
    private String usName;

    @Column(name = "us_email")
    private String usEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "us_role", nullable = false)
    private Role usRole;

    @Column(name = "us_profile", nullable = false)
    private String usProfile;
}
