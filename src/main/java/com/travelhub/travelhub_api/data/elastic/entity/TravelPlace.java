package com.travelhub.travelhub_api.data.elastic.entity;

import com.travelhub.travelhub_api.controller.place.response.PlaceResponse;
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

    public PlaceResponse ofPlaceResponse() {
        return PlaceResponse.builder()
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
        // Todo 장소 정보 추가 필요
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
