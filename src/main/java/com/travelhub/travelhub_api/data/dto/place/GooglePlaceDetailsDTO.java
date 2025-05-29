package com.travelhub.travelhub_api.data.dto.place;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GooglePlaceDetailsDTO {

    @JsonProperty("results")
    private List<Result> results;

    @Data
    public static class Result {
        @JsonProperty("address_components")
        private List<AddressComponent> addressComponents;
    }

    @Data
    public static class AddressComponent {
        @JsonProperty("long_name")
        private String longName;
        @JsonProperty("short_name")
        private String shortName;
        private List<String> types;
    }
}
