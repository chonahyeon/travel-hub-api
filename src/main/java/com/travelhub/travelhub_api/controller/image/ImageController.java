package com.travelhub.travelhub_api.controller.image;

import com.travelhub.travelhub_api.controller.image.response.BestImageResponse;
import com.travelhub.travelhub_api.data.enums.ImageType;
import com.travelhub.travelhub_api.service.image.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @PostMapping(value = UPLOAD, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String,List<String>> upload(HttpServletRequest request, @RequestParam(name = "igType") ImageType igType) {
        MultipartRequest multipartRequest = (MultipartRequest) request;
        MultiValueMap<String, MultipartFile> multiFileMap = multipartRequest.getMultiFileMap();
        Map<String, List<MultipartFile>> files = new HashMap<>(multiFileMap);
        return imageService.uploadImage(files, igType);
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
