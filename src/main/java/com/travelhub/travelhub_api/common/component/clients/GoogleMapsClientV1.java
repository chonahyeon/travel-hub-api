package com.travelhub.travelhub_api.common.component.clients;

import com.travelhub.travelhub_api.data.dto.place.GooglePlaceDetailsDTO;
import com.travelhub.travelhub_api.data.dto.place.GooglePlacesDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "mapsClient", url = "${google.maps-api-url-v1}")
public interface GoogleMapsClientV1 {

    @GetMapping(value = "${google.place-uri-v1}")
    GooglePlacesDTO getPlaces(@RequestParam("query") String query, @RequestParam("language") String language, @RequestParam("key") String key, @RequestParam("region") String region);

    @GetMapping(value = "${google.place-detail-uri}")
    GooglePlaceDetailsDTO getPlacesDetail(@RequestParam("place_id") String placeId, @RequestParam("fields") String fields, @RequestParam("language") String language, @RequestParam("key") String key);

    @GetMapping(value = "${google.place-detail-uri}")
    String getTest(@RequestParam("place_id") String placeId, @RequestParam("fields") String fields, @RequestParam("language") String language, @RequestParam("key") String key);

}