package com.travelhub.travelhub_api.service.image;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ImageServiceTest {

    @Autowired
    ImageService imageService;

    @Test
    void deleteImage() {
        imageService.deleteImage(1L);
    }
}
