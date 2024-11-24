package com.travelhub.travelhub_api.data.dto.place;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GooglePlacesResponse {

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

        @Data
        public static class Geometry {
            private Location location;

            @Data
            public static class Location {
                private double lat;
                private double lng;
            }
        }
    }
}
