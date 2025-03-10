package com.travelhub.travelhub_api.service.storage;

import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageAsyncClient;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails;
import com.oracle.bmc.objectstorage.model.PreauthenticatedRequest;
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.CreatePreauthenticatedRequestResponse;
import com.oracle.bmc.objectstorage.responses.PutObjectResponse;
import com.oracle.bmc.responses.AsyncHandler;
import com.oracle.bmc.retrier.RetryConfiguration;
import com.oracle.bmc.waiter.FixedTimeDelayStrategy;
import com.oracle.bmc.waiter.MaxAttemptsTerminationStrategy;
import com.travelhub.travelhub_api.common.resource.TravelHubResource;
import com.travelhub.travelhub_api.common.resource.exception.CustomException;
import com.travelhub.travelhub_api.common.util.FileUtil;
import com.travelhub.travelhub_api.data.dto.storage.UploadDTO;
import com.travelhub.travelhub_api.data.enums.common.ErrorCodes;
import com.travelhub.travelhub_api.data.mysql.entity.StorageEntity;
import com.travelhub.travelhub_api.data.mysql.repository.StorageRepository;
import com.travelhub.travelhub_api.service.common.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageService {

    private final StorageRepository storageRepository;
    private final RedisService<String, String> redisService;

    /**
     * 스토리지 클라이언트 생성
     *
     * @return object storage 클라이언트
     */
    private ObjectStorage getClient() throws IOException {
        AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider("~/.oci/config", "DEFAULT");
        return ObjectStorageClient.builder().build(provider);
    }

    /**
     * 스토리지 클라이언트 생성 (비동기)
     *
     * @return object storage 클라이언트
     */
    private ObjectStorageAsyncClient getAsyncClient() throws IOException {
        AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider("~/.oci/config", "DEFAULT");
        return ObjectStorageAsyncClient.builder().build(provider);
    }

    /**
     * 버킷에 요청 가능한 사전 인증된 요청 도메인 생성
     * 이미지 노출용으로만 사용.
     *
     * @param storageEntity 스토리지 정보
     */
    private String getPreAuthenticated(StorageEntity storageEntity) {
        try (ObjectStorage client = getClient()) {
            long now = System.currentTimeMillis();
            String preAuthenticatedName = String.format("%s_%s", storageEntity.getStName(), now);

            CreatePreauthenticatedRequestDetails details = CreatePreauthenticatedRequestDetails.builder()
                    .name(preAuthenticatedName)
                    .bucketListingAction(PreauthenticatedRequest.BucketListingAction.ListObjects)
                    .accessType(CreatePreauthenticatedRequestDetails.AccessType.AnyObjectRead)
                    .timeExpires(new Date(now + (60000 * 60 * 24)))
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

    /*
     * 스토리지 도메인 할당
     * todo : 현재는 스토리지 정보가 하나라 하드코딩으로 사용.
     */
    public String getImageDomain() {
        Optional<String> tempDomain = redisService.get(TravelHubResource.STORAGE_DOMAIN_KEY);

        // redis 에 저장된 정보가 없으면 사전 인증된 도메인 갱신.
        return tempDomain.orElseGet(() -> {
            StorageEntity storage = storageRepository.findById(1L)
                    .orElseThrow(() -> new CustomException(ErrorCodes.STORAGE_NOT_FOUND));
            return getPreAuthenticated(storage);
        });
    }

    /**
     * 다중 파일 업로드
     * @param uploadDTOS 업로드 정보
     */
    public void uploadFiles(List<UploadDTO> uploadDTOS) {
        StorageEntity storage = storageRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCodes.STORAGE_NOT_FOUND));

        try (ObjectStorageAsyncClient asyncClient = getAsyncClient())
        {
            CountDownLatch blockLatch = new CountDownLatch(uploadDTOS.size());
            for (UploadDTO uploadDTO : uploadDTOS) {
                uploadFile(asyncClient, uploadDTO, storage, blockLatch);
            }
            blockLatch.await();
        } catch (Exception e) {
            log.error("uploadFiles() : ", e);
        }
    }

    /**
     * 비동기 단건 파일 업로드
     * @param client 업로드 client
     * @param uploadDTO 업로드 정보
     * @param storage 스토리지 정보
     * @param block client 리소스 해제용
     */
    public void uploadFile(ObjectStorageAsyncClient client, UploadDTO uploadDTO, StorageEntity storage, CountDownLatch block) {
        try {
            // 재시도 정책
            RetryConfiguration retryConfig = RetryConfiguration.builder()
                    .terminationStrategy(new MaxAttemptsTerminationStrategy(3)) // 최대 3회 시도
                    .delayStrategy(new FixedTimeDelayStrategy(TimeUnit.SECONDS.toMillis(2))) // 2초 간격
                    .build();

            // 업로드 진행
            MultipartFile uploadFile = uploadDTO.getUploadFile();
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucketName(storage.getStName())
                    .retryConfiguration(retryConfig)
                    .objectName(uploadDTO.getUploadPath())
                    .contentLength(uploadFile.getSize())
                    .putObjectBody(uploadFile.getInputStream())
                    .opcClientRequestId(uploadDTO.getUniqueKey())
                    .build();

            client.putObject(request, new AsyncHandler<>() {
                @Override
                public void onSuccess(PutObjectRequest request, PutObjectResponse response) {
                    log.info("업로드 성공 ㅣ {}", uploadDTO.getUploadPath());
                    block.countDown();
                }

                @Override
                public void onError(PutObjectRequest request, Throwable error) {
                    log.error("업로드 실패 | {}", uploadDTO.getUploadPath(), error);
                    block.countDown();
                    // 실패건 재시도를 위해 local 에 write
                    FileUtil.createFile(uploadFile, uploadDTO.getUploadPath());
                }
            });

        } catch (Exception e) {
            log.error("uploadImage() : ", e);
        }
    }

    public void deleteFile(String deletePath) {
        StorageEntity storage = storageRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCodes.STORAGE_NOT_FOUND));

        try (ObjectStorage client = getClient()) {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucketName(storage.getStName())
                    .namespaceName(storage.getStNamespace())
                    .objectName(deletePath)
                    .build();

            client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            log.error("deleteImage() : ", e);
            throw new CustomException(ErrorCodes.SERVER_ERROR);
        }
    }
}
