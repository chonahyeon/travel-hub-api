package com.travelhub.travelhub_api.data.mysql.entity;

import com.travelhub.travelhub_api.data.enums.ImageType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "image")
@AllArgsConstructor
@Builder
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ig_idx")
    private Long igIdx;

    @Column(name = "idx", nullable = false)
    private Long idx;

    @Enumerated(EnumType.STRING)
    @Column(name = "ig_type", nullable = false)
    private ImageType igType;

    @Column(name = "ig_url", nullable = false)
    private String igUrl;

    @Column(name = "st_idx", nullable = false)
    private Long stIdx;
}
