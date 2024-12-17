package com.travelhub.travelhub_api.controller.image;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.travelhub.travelhub_api.common.resource.TravelHubResource.API_V1_IMAGE;

@RestController
@RequestMapping(API_V1_IMAGE)
@RequiredArgsConstructor
public class ImageController {

    @PostMapping("/upload")
    public void upload() {

    }
}
