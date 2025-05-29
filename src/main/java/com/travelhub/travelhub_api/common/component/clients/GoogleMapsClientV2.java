package com.travelhub.travelhub_api.common.component.clients;

import com.travelhub.travelhub_api.common.configuration.web.GoogleMapHeaderConfiguration;
import com.travelhub.travelhub_api.data.dto.place.GooglePlaceRequestDTO;
import com.travelhub.travelhub_api.data.dto.place.GooglePlacesV2DTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        value = "googleMapClient",
        url = "${google.maps-api-url-v2}",
        configuration = GoogleMapHeaderConfiguration.class
)
public interface GoogleMapsClientV2 {

    @PostMapping(value = "${google.place-uri-v2}")
    GooglePlacesV2DTO getPlaces(
            @RequestHeader("X-Goog-FieldMask") String fields,
            @RequestBody GooglePlaceRequestDTO body
    );
}