package com.travelhub.travelhub_api.data.elastic.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Builder
@Document(indexName = "dev_travel")
public class TravelPlace {
    @Id
    private String pcId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long pcIdx;
    private String pcName;
    private String pcAddress;
    private Double pcRating;
    private double pcLng;
    private double pcLat;
}
