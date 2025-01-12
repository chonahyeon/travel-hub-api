package com.travelhub.travelhub_api.service.image;

import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.PutObjectResponse;
import com.travelhub.travelhub_api.common.resource.exception.CustomException;
import com.travelhub.travelhub_api.controller.image.request.ImageCreateRequest;
import com.travelhub.travelhub_api.data.enums.common.ErrorCodes;
import com.travelhub.travelhub_api.data.mysql.entity.ImageEntity;
import com.travelhub.travelhub_api.data.mysql.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    /*
     * 스토리지 인증 객체.
     */
    private ObjectStorage getClient() throws IOException {
        AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider("~/.oci/config", "DEFAULT");
        return ObjectStorageClient.builder()
                .build(provider);
    }

    /*
     *  이미지 업로드
     */
    public void uploadImage(MultipartFile image) {
        try (ObjectStorage client = getClient())
        {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucketName("버킷명")
                    .objectName("파일명")
                    .contentLength(image.getSize())
                    .putObjectBody(image.getInputStream())
                    .build();

            PutObjectResponse putObjectResponse = client.putObject(request);
            log.info("업로드 결과 : {}", putObjectResponse);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    /*
     * bucket = travel-hub-bucket
     * namespace = ax3mptjyjreo
     * object name = review/rion_chunsik.png
     */
    public void deleteImage(Long igIdx) {
        ImageEntity imageEntity = imageRepository.findById(igIdx)
                .orElseThrow(() -> new CustomException(ErrorCodes.INVALID_PARAM, "igIdx"));

        try (ObjectStorage client = getClient()) {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
//                    .bucketName()
                    .namespaceName("ax3mptjyjreo")
                    .objectName("review/rion_chunsik.png")
                    .build();

            client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
