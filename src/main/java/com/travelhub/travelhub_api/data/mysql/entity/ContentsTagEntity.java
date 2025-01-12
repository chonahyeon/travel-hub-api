package com.travelhub.travelhub_api.data.mysql.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "contents_tag")
public class ContentsTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ctg_idx")
    private Long ctgIdx;

    @Column(name = "ct_idx")
    private Long ctIdx;

    @Column(name = "tg_idx")
    private Long tgIdx;
}
