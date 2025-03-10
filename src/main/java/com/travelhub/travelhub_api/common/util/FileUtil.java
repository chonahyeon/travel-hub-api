package com.travelhub.travelhub_api.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileUtil {

    public static void createFile(MultipartFile file, String path) {
        Path uploadPath = Paths.get(path);
        Path dirPath = uploadPath.getParent();
        try {
            // 폴더 생성
            Files.createDirectories(dirPath);
            // Write
            Files.write(uploadPath, file.getBytes());
        } catch (Exception e) {
            log.error("createFile : ", e);
        }
    }
}
