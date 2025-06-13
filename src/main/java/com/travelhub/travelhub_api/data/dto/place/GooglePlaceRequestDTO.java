package com.travelhub.travelhub_api.data.dto.place;

import com.travelhub.travelhub_api.data.mysql.entity.CountryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GooglePlaceRequestDTO {

    private String textQuery;
    @Builder.Default
    private String languageCode = "ko"; // 구글맵 지원 regionCode, languageCode 규격이 다름
    private String regionCode;
    private LocationRestriction locationRestriction;

    /**
     * IP 편향 방지 추가
     */
    @Data
    @Builder
    public static class LocationRestriction {
        private Rectangle rectangle;

        public static LocationRestriction of(CountryEntity country) {
            Location low = Location.builder()
                    .latitude(country.getCntLowLat())
                    .longitude(country.getCntLowLng())
                    .build();

            Location high = Location.builder()
                    .latitude(country.getCntHighLat())
                    .longitude(country.getCntHighLng())
                    .build();

            Rectangle rectangle = Rectangle.builder()
                    .low(low)
                    .high(high)
                    .build();

            return LocationRestriction.builder().rectangle(rectangle).build();
        }

        @Data
        @Builder
        public static class Rectangle {
            private Location low;
            private Location high;
        }

        @Data
        @Builder
        public static class Location {
            private Double latitude;
            private Double longitude;
        }
    }

    public static GooglePlaceRequestDTO of(String name, CountryEntity country) {
        return GooglePlaceRequestDTO.builder()
                .textQuery(name)
                .regionCode(country.getCntCode())
                .locationRestriction(LocationRestriction.of(country))
                .build();
    }
}
