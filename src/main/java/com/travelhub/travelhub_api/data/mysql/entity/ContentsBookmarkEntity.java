package com.travelhub.travelhub_api.data.mysql.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contents_bookmark")
public class ContentsBookmarkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cb_idx", nullable = false)
    private Long cbidx;

    @Column(name = "us_id", nullable = false)
    private String us_id;

    @Column(name = "us_id", nullable = false)
    private String usId;

    @Column(name = "ct_idx", nullable = false)
    private Long ctIdx;
}