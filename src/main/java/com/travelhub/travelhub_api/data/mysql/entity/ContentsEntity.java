package com.travelhub.travelhub_api.data.mysql.entity;

import com.travelhub.travelhub_api.data.mysql.entity.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contents")
public class ContentsEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ct_idx")
    private Long ctIdx;

    @Column(name = "ct_score")
    private Double ctScore;

    @Column(name = "ct_title", nullable = false)
    private String ctTitle;

    @Column(name = "ct_text")
    private String ctText;

    @Column(name = "us_id", nullable = false)
    private String usId;

    public void update(ContentsRequest request) {
        this.ctTitle = request.title();
        if (!request.text().isBlank()) {
            this.ctText = request.text();
        }
    }

    public ContentsListResponse ofContentsListResponse() {
        return ContentsListResponse.builder()
                .ctIdx(this.ctIdx)
                .ctTitle(this.ctTitle)
                .uId(this.usId)
                .ctScore(this.ctScore)
                .build();
    }

    public void updateScore(Double score) {
        this.ctScore = score;
    }
}
