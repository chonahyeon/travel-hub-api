package com.travelhub.travelhub_api.data.mysql.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "place")
public class PlaceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pc_idx")
    private Long pcIdx;

    @Column(name = "pc_name")
    private String pcName;

    @Column(name = "pc_address")
    private String pcAddress;

    @Column(name = "pc_rating")
    private Double pcRating;

    @Column(name = "pc_lng")
    private Double pcLng;

    @Column(name = "pc_lat")
    private Double pcLat;

    @Column(name = "pc_id")
    private String pcId;

}
