package com.travelhub.travelhub_api.data.mysql.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class ContentsBookmarkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cb_idx", nullable = false)
    private Long cb_idx;

    @Column(name = "u_idx", nullable = false)
    private Long u_idx;

    @Column(name = "ct_idx", nullable = false)
    private Long ctIdx;
}