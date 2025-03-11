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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
     * @param images    멀티 파트
     * @param igType 이미지 타입 (RV, CT) / 매핑 idx
     */
    public List<String> uploadImage(List<MultipartFile> images, ImageType igType) {
        // 업로드 정보
        List<UploadDTO> uploadDTOS = new ArrayList<>();
        // 임의로 경로 먼저 생성
        List<String> randomPath = getRandomPath(igType, images, uploadDTOS);
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
    private List<String> getRandomPath(ImageType imageType, List<MultipartFile> images, List<UploadDTO> uploadDTOS) {
        LocalDate now = LocalDate.now();
        String parent = now.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "/";
        List<String> randomPaths = new ArrayList<>();

        for (MultipartFile image : images) {
            String fileName = UUID.randomUUID() + ".jpg";
            String uploadPath = imageType.getUploadPath() + parent + fileName;

            try {
                UploadDTO uploadDTO = UploadDTO.builder()
                        .uploadPath(uploadPath)
                        .uploadFileStream(image.getInputStream())
                        .size(image.getSize())
                        .build();

                randomPaths.add(uploadPath);
                uploadDTOS.add(uploadDTO);
            } catch (Exception e) {
                log.error("getRandomPath() : 파일 스트림 할당 실패 ", e);
                throw new CustomException(ErrorCodes.SERVER_ERROR);
            }
        }

        return randomPaths;
    }
}
