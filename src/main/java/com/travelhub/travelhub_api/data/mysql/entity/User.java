package com.travelhub.travelhub_api.data.mysql.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "u_idx")
    private Long uIdx;

    @Column(name = "u_id", nullable = false)
    private String uId;

}
