package com.travelhub.travelhub_api.data.mysql.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "storage")
public class StorageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "st_idx")
    private Long stIdx;

    @Column(name = "st_name")
    private String stName;

    @Column(name = "st_region")
    private String stRegion;

    @Column(name = "st_namespace")
    private String stNamespace;
}
