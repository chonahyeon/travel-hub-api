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
@Table(name = "contents_tag")
public class ContentsTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ctg_idx")
    private Long ctgIdx;

    @Column(name = "tg_idx")
    private Long tgIdx;

    @Column(name = "ct_idx")
    private Long ctIdx;

}
