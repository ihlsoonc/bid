package com.bidsystem.bid.service;

import com.bidsystem.bid.service.ExceptionService.*;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileService {

    private Path uploadDir = Paths.get("src/main/resources/images/uploads");

    public FileService() throws IOException {
        // 디렉토리가 존재하지 않으면 생성
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
    }

    // 파일 업로드
    public String uploadFile(MultipartFile file) throws IOException {
        Path targetLocation = uploadDir.resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        return file.getOriginalFilename();
    }

    // 파일 다운로드
    public Resource downloadFile(String fileName) throws MalformedURLException {
        Path filePath = uploadDir.resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists()) {
            return resource;
        } else {
            throw new NotFoundException("다운로드 파일을 찾을 수 없습니다: " + fileName);
        }
    }
}
