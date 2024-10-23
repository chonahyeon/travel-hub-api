package com.travelhub.travelhub_api.data.mysql.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pc_idx")
    private Long pcIdx;

    @Column(name = "pc_lng")
    private String pc_lng;

    @Column(name = "pc_lat")
    private String pc_lat;

    @Column(name = "pc_name")
    private String pc_name;

    @Column(name = "pc_rate")
    private Double pcRate;

}
