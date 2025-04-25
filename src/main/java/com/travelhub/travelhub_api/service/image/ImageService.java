package com.travelhub.travelhub_api.service.image;

import com.travelhub.travelhub_api.common.resource.exception.CustomException;
import com.travelhub.travelhub_api.controller.image.response.BestImageResponse;
import com.travelhub.travelhub_api.data.dto.image.BestImageListDTO;
import com.travelhub.travelhub_api.data.dto.storage.UploadDTO;
import com.travelhub.travelhub_api.data.enums.ImageType;
import com.travelhub.travelhub_api.data.enums.common.ErrorCodes;
import com.travelhub.travelhub_api.data.mysql.entity.ImageEntity;
import com.travelhub.travelhub_api.data.mysql.repository.ImageRepository;
import com.travelhub.travelhub_api.data.mysql.support.ImageRepositorySupport;
import com.travelhub.travelhub_api.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final ImageRepositorySupport imageRepositorySupport;
    private final StorageService storageService;

    /**
     * 베스트 이미지 조회
     *
     * @param citIdx 도시 idx
     * @param limit  캐러셀 이미지 개수 (컨텐츠당 1개)
     * @return 이미지 목록
     */
    @Transactional(readOnly = true)
    public BestImageResponse findBestImages(Long citIdx, Integer limit) {
        List<BestImageListDTO> contentBestImages = imageRepositorySupport.findContentBestImages(citIdx, limit);
        String domain = storageService.getImageDomain();
        return BestImageResponse.of(contentBestImages, domain);
    }

    /**
     * 이미지 업로드 진행
     *
     * @param imageMap    멀티 파트
     * @param igType 이미지 타입 (RV, CT) / 매핑 idx
     */
    public Map<String, List<String>> uploadImage(Map<String, List<MultipartFile>> imageMap, ImageType igType) {
        // 업로드 정보
        List<UploadDTO> uploadDTOS = new ArrayList<>();
        // 임의로 경로 먼저 생성
        Map<String, List<String>> randomPath = getRandomPath(igType, imageMap, uploadDTOS);
        storageService.uploadFiles(uploadDTOS);
        return randomPath;
    }

    /**
     * 이미지 삭제
     * @param igIdx 삭제 대상 이미지 idx
     */
    @Transactional
    public void deleteImage(Long igIdx) {
        ImageEntity imageEntity = imageRepository.findById(igIdx)
                .orElseThrow(() -> new CustomException(ErrorCodes.INVALID_PARAM, "igIdx"));
        storageService.deleteFile(imageEntity.getIgPath());
        imageRepository.deleteById(imageEntity.getIgIdx());
    }

    /*
     * 이미지 업로드 경로 생성 (이미지 타입별 root 경로 다름)
     */
    private Map<String, List<String>> getRandomPath(ImageType imageType, Map<String, List<MultipartFile>> imageMap, List<UploadDTO> uploadDTOS) {
        LocalDate now = LocalDate.now();
        String parent = now.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "/";
        Map<String, List<String>> randomPaths = new HashMap<>();

        for (String place : imageMap.keySet()) {
            List<String> returnImages = randomPaths.getOrDefault(place, new ArrayList<>());
            List<MultipartFile> multipartFiles = imageMap.get(place);

            for (MultipartFile image : multipartFiles) {
                String fileName = UUID.randomUUID() + ".jpg";
                String uploadPath = imageType.getUploadPath() + parent + fileName;

                try {
                    UploadDTO uploadDTO = UploadDTO.builder()
                            .uploadPath(uploadPath)
                            .uploadFileStream(new BufferedInputStream(image.getInputStream()))
                            .size(image.getSize())
                            .build();

                    returnImages.add(uploadPath);
                    uploadDTOS.add(uploadDTO);
                } catch (Exception e) {
                    log.error("getRandomPath() : 파일 스트림 할당 실패 ", e);
                    throw new CustomException(ErrorCodes.SERVER_ERROR);
                }
            }
            randomPaths.put(place, returnImages);
        }


        return randomPaths;
    }
}
