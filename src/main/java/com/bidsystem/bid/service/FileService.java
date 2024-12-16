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

/* 주요 메서드 설명 */
/* 1. 생성자 FileService():
      - 파일 업로드 디렉토리가 존재하지 않을 경우 디렉토리를 생성
      - 기본 업로드 경로: `src/main/resources/images/uploads` */

/* 2. uploadFile(MultipartFile file):
      - 클라이언트로부터 업로드된 파일을 지정된 디렉토리에 저장
      - 동일한 파일 이름이 이미 존재하면 기존 파일을 덮어씌움
      - 저장된 파일 이름을 반환 */

/* 3. downloadFile(String fileName):
      - 요청된 파일 이름에 해당하는 리소스를 다운로드
      - 파일이 존재하지 않을 경우 `NotFoundException`을 발생시킴
      - 반환 타입: `Resource` 객체, 파일의 경로를 기반으로 생성 */

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
