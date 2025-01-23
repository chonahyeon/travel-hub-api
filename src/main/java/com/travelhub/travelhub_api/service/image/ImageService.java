package com.travelhub.travelhub_api.service.image;

import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails;
import com.oracle.bmc.objectstorage.model.PreauthenticatedRequest;
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.CreatePreauthenticatedRequestResponse;
import com.oracle.bmc.objectstorage.responses.PutObjectResponse;
import com.travelhub.travelhub_api.common.resource.TravelHubResource;
import com.travelhub.travelhub_api.common.resource.exception.CustomException;
import com.travelhub.travelhub_api.controller.image.response.ImageTypeListResponse;
import com.travelhub.travelhub_api.data.enums.ImageType;
import com.travelhub.travelhub_api.data.enums.common.ErrorCodes;
import com.travelhub.travelhub_api.data.mysql.entity.ImageEntity;
import com.travelhub.travelhub_api.data.mysql.entity.StorageEntity;
import com.travelhub.travelhub_api.data.mysql.repository.ImageRepository;
import com.travelhub.travelhub_api.data.mysql.repository.StorageRepository;
import com.travelhub.travelhub_api.service.common.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final StorageRepository storageRepository;
    private final RedisService<String,String> redisService;

    /**
     * 스토리지 클라이언트 생성
     * @return object storage 클라이언트
     */
    private ObjectStorage getClient() throws IOException {
        AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider("~/.oci/config", "DEFAULT");
        return ObjectStorageClient.builder().build(provider);
    }

    /**
     * 버킷에 요청 가능한 사전 인증된 요청 도메인 생성
     * 이미지 노출용으로만 사용.
     * @param storageEntity 스토리지 정보
     * @param rootPath 권한을 생성할 폴더
     */
    private String getPreAuthenticated(StorageEntity storageEntity, String rootPath) {
        try (ObjectStorage client = getClient()){
            long now = System.currentTimeMillis();
            String preAuthenticatedName = String.format("%s_%s", storageEntity.getStName(), now);

            CreatePreauthenticatedRequestDetails details = CreatePreauthenticatedRequestDetails.builder()
                    .name(preAuthenticatedName)
                    .bucketListingAction(PreauthenticatedRequest.BucketListingAction.ListObjects)
                    .accessType(CreatePreauthenticatedRequestDetails.AccessType.AnyObjectRead)
                    .objectName(rootPath)
                    .timeExpires(new Date(now + (60000 * 60 * 3)))
                    .build();

            CreatePreauthenticatedRequestRequest request = CreatePreauthenticatedRequestRequest.builder()
                    .namespaceName(storageEntity.getStNamespace())
                    .bucketName(storageEntity.getStName())
                    .createPreauthenticatedRequestDetails(details)
                    .build();

            CreatePreauthenticatedRequestResponse preAuthenticatedResponse = client.createPreauthenticatedRequest(request);
            PreauthenticatedRequest preauthenticatedRequest = preAuthenticatedResponse.getPreauthenticatedRequest();

            // redis 갱신
            String preAuthenticatedDomain = preauthenticatedRequest.getFullPath();
            redisService.save(TravelHubResource.STORAGE_DOMAIN_KEY, preAuthenticatedDomain);

            log.info("갱신 완료 : {}", preAuthenticatedDomain);
            return preAuthenticatedDomain;
        } catch (Exception e) {
            log.error("getImageList() : ", e);
            throw new CustomException(ErrorCodes.SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public List<ImageTypeListResponse> findImages(ImageType imageType) {
        StorageEntity storage = storageRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCodes.STORAGE_NOT_FOUND));

        Optional<String> tempDomain = redisService.get(TravelHubResource.STORAGE_DOMAIN_KEY);
        String domain = tempDomain.orElseGet(() -> getPreAuthenticated(storage, imageType.getUploadPath()));

        List<ImageEntity> imageTypeList = imageRepository.findByIgType(imageType);
        return ImageTypeListResponse.ofList(imageTypeList, domain);
    }

    /**
     * 이미지 업로드 진행
     * @param image 멀티 파트
     * @param imageType 이미지 타입 (RV, CT)
     */
    public void uploadImage(MultipartFile image, ImageType imageType) {
        StorageEntity storage = storageRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCodes.STORAGE_NOT_FOUND));

        try (ObjectStorage client = getClient())
        {
            String uploadPath = getRandomPath(imageType);

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucketName(storage.getStName())
                    .objectName(uploadPath)
                    .contentLength(image.getSize())
                    .putObjectBody(image.getInputStream())
                    .build();

            PutObjectResponse putObjectResponse = client.putObject(request);
            log.info("업로드 결과 : {}", putObjectResponse);

            ImageEntity imageEntity = ImageEntity.builder()
                    .igType(imageType)
                    .igPath(uploadPath)
                    .stIdx(storage.getStIdx())
                    .build();

            imageRepository.save(imageEntity);
        } catch (Exception e) {
            log.error("uploadImage() : ", e);
            throw new CustomException(ErrorCodes.SERVER_ERROR);
        }
    }

    /**
     * 이미지 삭제
     * @param igIdx 삭제 대상 이미지 idx
     */
    public void deleteImage(Long igIdx) {
        ImageEntity imageEntity = imageRepository.findById(igIdx)
                .orElseThrow(() -> new CustomException(ErrorCodes.INVALID_PARAM, "igIdx"));

        Long stIdx = imageEntity.getStIdx();
        StorageEntity storageEntity = storageRepository.findById(stIdx)
                .orElseThrow(() -> new CustomException(ErrorCodes.INVALID_PARAM, "stIdx"));

        try (ObjectStorage client = getClient()) {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucketName(storageEntity.getStName())
                    .namespaceName(storageEntity.getStNamespace())
                    .objectName(imageEntity.getIgPath())
                    .build();

            client.deleteObject(deleteObjectRequest);
            imageRepository.deleteById(igIdx);
        } catch (Exception e) {
            log.error("deleteImage() : ", e);
            throw new CustomException(ErrorCodes.SERVER_ERROR);
        }
    }

    /*
     * 이미지 업로드 경로 생성 (이미지 타입별 root 경로 다름)
     */
    private String getRandomPath(ImageType imageType) {
        LocalDate now = LocalDate.now();
        String parent = now.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "/";
        UUID imageName = UUID.randomUUID();
        return imageType.getUploadPath() + parent + imageName + ".jpg";
    }
}
