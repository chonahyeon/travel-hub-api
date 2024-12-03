package com.travelhub.travelhub_api.common.component.clients;

import com.travelhub.travelhub_api.data.dto.place.GooglePlacesResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "mapsClient", url = "${google.maps-api-url}")
public interface GoogleMapsClient {

    @GetMapping(value = "${google.place-uri}")
    GooglePlacesResponse getPlaces(@RequestParam("query") String query, @RequestParam("language") String language, @RequestParam("key") String key);
}