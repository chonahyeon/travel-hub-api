package com.travelhub.travelhub_api.data.mysql.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "city")
public class CityEntity {
    @Id
    @Column(name = "cit_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long citIdx;

    @Column(name = "cnt_idx")
    Long cntIdx;

    @Column(name = "cit_name")
    String citName;
}
