package com.travelhub.travelhub_api.service.image;

import com.travelhub.travelhub_api.data.mysql.entity.ImageEntity;
import com.travelhub.travelhub_api.data.mysql.repository.ImageRepository;
import com.travelhub.travelhub_api.data.mysql.repository.StorageRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
@Slf4j
public class ImageServiceTest {

    @Autowired
    ImageService imageService;

    @Autowired
    StorageRepository storageRepository;

    @Autowired
    ImageRepository imageRepository;

    @Test
    public void findImage() {
        Optional<ImageEntity> byId = imageRepository.findById(2L);
        ImageEntity imageEntity = byId.orElseThrow(RuntimeException::new);
        log.info("{}", imageEntity);
    }
}
