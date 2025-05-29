package com.travelhub.travelhub_api.data.dto.place;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GooglePlaceRequestDTO {
    /*
     * 구글맵 지원 regionCode, languageCode 규격이 다름
     * 각각 kr, ko 지정해야함.
     */
    private String textQuery;
    private String regionCode;
    @Builder.Default
    private String languageCode = "ko";

    public static GooglePlaceRequestDTO of(String name, String regionCode) {
        return GooglePlaceRequestDTO.builder()
                .textQuery(name)
                .regionCode(regionCode)
                .build();
    }
}
