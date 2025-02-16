package com.travelhub.travelhub_api.common.component.clients;

import com.travelhub.travelhub_api.data.dto.place.GooglePlaceDetailsDto;
import com.travelhub.travelhub_api.data.dto.place.GooglePlacesDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "mapsClient", url = "${google.maps-api-url}")
public interface GoogleMapsClient {

    @GetMapping(value = "${google.place-uri}")
    GooglePlacesDTO getPlaces(@RequestParam("query") String query, @RequestParam("language") String language, @RequestParam("key") String key);

    @GetMapping(value = "${google.place-detail-uri}")
    GooglePlaceDetailsDto getPlacesDetail(@RequestParam("place_id") String placeId, @RequestParam("fields") String fields, @RequestParam("language") String language, @RequestParam("key") String key);

}