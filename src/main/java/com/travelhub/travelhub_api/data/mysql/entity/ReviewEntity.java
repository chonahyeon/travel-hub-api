package com.travelhub.travelhub_api.data.mysql.entity;

import com.travelhub.travelhub_api.data.mysql.entity.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ReviewEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rv_idx")
    private Long rvIdx;

    @Column(name = "rv_score", nullable = false)
    @Builder.Default
    private Double rvScore = 0.0;

    @Column(name = "rv_text", nullable = false)
    private String rvText;

    @Column(name = "u_id", nullable = false)
    private String uId;

    @Column(name = "ct_idx")
    private Long ctIdx;

}
