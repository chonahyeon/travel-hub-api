package com.travelhub.travelhub_api.data.mysql.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class Contents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ct_idx")
    private Long ctIdx;

    @Column(name = "ct_score")
    private Double ctScore;

    @Column(name = "ct_text")
    private String ctText;

    @Column(name = "ct_insert_date", nullable = false)
    private LocalDateTime ctInsertDate;

    @Column(name = "ct_update_date", nullable = false)
    private LocalDateTime ctUpdateDate;

    @Column(name = "u_id", nullable = false)
    private String uId;


}
