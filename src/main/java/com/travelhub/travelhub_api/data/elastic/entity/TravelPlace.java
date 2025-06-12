package com.travelhub.travelhub_api.data.elastic.entity;

import com.travelhub.travelhub_api.controller.place.response.ContentsPlaceResponse;
import com.travelhub.travelhub_api.data.mysql.entity.PlaceEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Builder
@ToString
@Document(indexName = "dev_travel")
public class TravelPlace {
    @Id
    private String pcId;
    private String pcName;
    private String pcAddress;
    private Double pcRating;
    private double pcLng;
    private double pcLat;
    private String compoundCode;
    private String citName;

    public ContentsPlaceResponse ofPlaceResponse() {
        return ContentsPlaceResponse.builder()
                .pcId(this.pcId)
                .pcName(this.pcName)
                .pcAddress(this.pcAddress)
                .pcRating(this.pcRating)
                .pcLng(this.pcLng)
                .pcLat(this.pcLat)
                .citName(this.citName)
                .build();
    }

    public PlaceEntity ofPlaceEntity(Long citIdx) {
        return PlaceEntity.builder()
                .pcName(this.pcName)
                .pcAddress(this.pcAddress)
                .pcRating(this.pcRating)
                .pcLng(this.pcLng)
                .pcLat(this.pcLat)
                .pcId(this.pcId)
                .citIdx(citIdx)
                .build();
    }
}
