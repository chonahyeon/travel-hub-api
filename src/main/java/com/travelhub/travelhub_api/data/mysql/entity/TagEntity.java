package com.travelhub.travelhub_api.data.mysql.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tg_idx")
    private Long tgIdx;

    @Column(name = "tg_name", nullable = false)
    private String tgName;

    @Column(name = "ct_idx", nullable = false)
    private Long ctIdx;

}
