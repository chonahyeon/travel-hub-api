package com.travelhub.travelhub_api.data.mysql.entity;

import com.travelhub.travelhub_api.data.enums.ContentsPlaceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class ContentsPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cp_idx")
    private Long cpIdx;

    @Enumerated(EnumType.STRING)
    @Column(name = "cp_type")
    private ContentsPlaceType cpType;

    @Column(name = "cp_order")
    private Integer cpOrder;

    @Column(name = "pc_idx")
    private Long pcIdx;

}
