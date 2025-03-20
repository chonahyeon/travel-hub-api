package com.travelhub.travelhub_api.data.mysql.entity;

import com.travelhub.travelhub_api.data.enums.ImageType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "image")
@AllArgsConstructor
@Builder
@ToString
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ig_idx")
    private Long igIdx;

    @Column(name = "idx")
    private Long idx;

    @Enumerated(EnumType.STRING)
    @Column(name = "ig_type", nullable = false)
    private ImageType igType;

    @Column(name = "ig_path", nullable = false)
    private String igPath;

    @Column(name = "st_idx", nullable = false)
    @Builder.Default
    private Long stIdx = 1L;    // (2025.03.13) 현재는 1만 사용

    public void updateImagePath(String igPath) {
        this.igPath = igPath;
    }

    public void updateIgPath(String paths) {
        this.igPath = paths;
    }
}
