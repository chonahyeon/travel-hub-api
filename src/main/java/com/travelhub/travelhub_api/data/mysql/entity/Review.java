package com.travelhub.travelhub_api.data.mysql.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rv_idx")
    private Long rvIdx;

    @Column(name = "rv_score", nullable = false)
    private Double rvScore;

    @Column(name = "rv_text", nullable = false)
    private String rvText;

    @Column(name = "rv_insert_date", nullable = false)
    private LocalDateTime rvInsertDate;

    @Column(name = "rv_update_date", nullable = false)
    private LocalDateTime rvUpdateDate;

    @Column(name = "u_id", nullable = false)
    private String uId;

    @Column(name = "ct_idx")
    private Long ctIdx;

}
