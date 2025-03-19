package com.travelhub.travelhub_api.data.mysql.entity;

import com.travelhub.travelhub_api.data.dto.contents.ContentsPlaceWriterDto;
import com.travelhub.travelhub_api.data.enums.ContentsPlaceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contents_place")
public class ContentsPlaceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cp_idx")
    private Long cpIdx;

    @Enumerated(EnumType.STRING)
    @Column(name = "cp_type")
    private ContentsPlaceType cpType;

    @Column(name = "cp_order")
    private Integer cpOrder;

    @Column(name = "cp_text")
    private String cpText;

    @Column(name = "pc_idx")
    private Long pcIdx;

    @Column(name = "ct_idx")
    private Long ctIdx;
}
