package com.travelhub.travelhub_api.controller.image;

import com.travelhub.travelhub_api.controller.image.response.ImageTypeListResponse;
import com.travelhub.travelhub_api.data.enums.ImageType;
import com.travelhub.travelhub_api.service.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.travelhub.travelhub_api.common.resource.TravelHubResource.*;

@RestController
@RequestMapping(API_V1_IMAGE)
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping(value = UPLOAD, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void upload(@RequestPart MultipartFile multipartFile, @RequestParam ImageType igType) {
        imageService.uploadImage(multipartFile, igType);
    }

    @DeleteMapping("/{igIdx}")
    public void deleteImage(@PathVariable Long igIdx) {
        imageService.deleteImage(igIdx);
    }

    @GetMapping(LIST)
    public List<ImageTypeListResponse> findImages(@RequestParam ImageType imageType) {
        return imageService.findImages(imageType);
    }
}
