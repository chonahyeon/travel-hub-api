package com.travelhub.travelhub_api.controller.image;

import com.travelhub.travelhub_api.controller.image.request.ImageCreateRequest;
import com.travelhub.travelhub_api.controller.image.response.BestImageResponse;
import com.travelhub.travelhub_api.data.enums.ImageType;
import com.travelhub.travelhub_api.service.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.travelhub.travelhub_api.common.resource.TravelHubResource.*;

@RestController
@RequestMapping(API_V1_IMAGE)
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    /**
     * 이미지 업로드
     * POST /travel/v1/image/upload
     */
    @PostMapping(value = UPLOAD, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void upload(@RequestPart MultipartFile file, @RequestPart ImageCreateRequest request) {
        imageService.uploadImage(file, request);
    }

    /**
     * 이미지 삭제
     * DELETE /travel/v1/image/{igIdx}
     * @param igIdx 이미지 idx
     */
    @DeleteMapping("/{igIdx}")
    public void deleteImage(@PathVariable Long igIdx) {
        imageService.deleteImage(igIdx);
    }

    /**
     * 도시 별 베스트 게시물 이미지 조회
     * GET /travel/v1/image/best/list/{citIdx}
     * @param citIdx 도시 idx
     * @param limit 캐러셀에 노출할 최대 이미지 개수
     */
    @GetMapping(BEST + LIST + "/{citIdx}")
    public BestImageResponse findBestImages(@PathVariable Long citIdx, @RequestParam Integer limit) {
        return imageService.findBestImages(citIdx, limit);
    }
}
