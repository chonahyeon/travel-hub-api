package com.travelhub.travelhub_api.data.mysql.entity;

import com.travelhub.travelhub_api.data.mysql.entity.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "contents")
public class ContentsEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ct_idx")
    private Long ctIdx;

    @Column(name = "ct_score")
    private Double ctScore;

    @Column(name = "ct_text")
    private String ctText;

    @Column(name = "u_id", nullable = false)
    private String uId;
}
