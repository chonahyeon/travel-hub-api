package com.travelhub.travelhub_api.data.mysql.entity;

//import com.travelhub.travelhub_api.controller.contents.request.ContentsRequest;
//import com.travelhub.travelhub_api.controller.contents.response.ContentsListResponse;
import com.travelhub.travelhub_api.data.mysql.entity.common.BaseTimeEntity;
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

    @Column(name = "us_id", nullable = false)
    private String usId;

    @Column(name = "ct_view_count", nullable = false)
    private Long ctViewCount;

//    public void update(ContentsRequest request) {
//        this.ctTitle = request.title();
//        if (!request.text().isBlank()) {
//            this.ctText = request.text();
//        }
//    }

//    public ContentsListResponse ofContentsListResponse() {
//        return ContentsListResponse.builder()
//                .ctIdx(this.ctIdx)
//                .ctTitle(this.ctTitle)
//                .uId(this.usId)
//                .ctScore(this.ctScore)
//                .build();
//    }

    public void updateScore(Double score) {
        this.ctScore = score;
    }
}
