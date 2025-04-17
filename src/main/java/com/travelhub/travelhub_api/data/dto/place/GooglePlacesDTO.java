package com.travelhub.travelhub_api.data.dto.place;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GooglePlacesDTO {

    @JsonProperty("results")
    private List<PlaceResultDto> results;

    @Data
    public static class PlaceResultDto {
        private String name;

        @JsonProperty("formatted_address")
        private String formattedAddress;

        private double rating;

        @JsonProperty("place_id")
        private String placeId;

        private Geometry geometry;

        @JsonProperty("plus_code")
        private PlusCode plusCode;

        @Data
        public static class Geometry {
            private Location location;

            @Data
            public static class Location {
                private double lat;
                private double lng;
            }
        }

        @Data
        public static class PlusCode {
            @JsonProperty("compound_code")
            private String compoundCode;
            @JsonProperty("global_code")
            private String globalCode;
        }
    }
}
