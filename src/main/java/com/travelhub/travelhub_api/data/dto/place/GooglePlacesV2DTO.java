package com.travelhub.travelhub_api.data.dto.place;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.travelhub.travelhub_api.data.elastic.entity.TravelPlace;
import lombok.Data;

import java.util.List;

@Data
public class GooglePlacesV2DTO {

    @JsonProperty(value = "places")
    private List<Places> places;

    @Data
    public static class Places {
        private String id;
        private String formattedAddress;
        private List<AddressComponents> addressComponents;
        private Location location;
        private double rating;
        private DisplayName displayName;
        private List<Photos> photos;
        private PlusCode plusCode;

        public TravelPlace of(String citName, String citLangCode) {
            return TravelPlace.builder()
                    .pcId(this.id)
                    .pcName(this.displayName.getText())
                    .pcAddress(this.formattedAddress)
                    .pcRating(this.rating)
                    .pcLat(this.location.latitude)
                    .pcLng(this.location.longitude)
//                    .compoundCode(this.plusCode.compoundCode)
                    .citName(citName)
                    .citLangCode(citLangCode)
                    .build();
        }
    }

    @Data
    public static class AddressComponents {
        private String longText;
        private String shortText;
        private List<String> types;
        private String languageCode;
    }

    @Data
    public static class Location {
        private double latitude;
        private double longitude;
    }

    @Data
    public static class DisplayName {
        private String text;
    }

    @Data
    public static class Photos {
        private String name;
        private Integer widthPx;
        private Integer heightPx;
    }

    @Data
    public static class PlusCode {
        private String globalCode;
        private String compoundCode;
    }
}
