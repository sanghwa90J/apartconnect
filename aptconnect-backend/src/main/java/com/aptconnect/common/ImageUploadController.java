package com.aptconnect.common;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ImageUploadController {
    @PostMapping("/upload-image")
    public Map<String, String> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String uploadDir = System.getProperty("user.dir") + "/uploads";
            Path savePath = Paths.get(uploadDir, filename);
            Files.createDirectories(savePath.getParent());
            file.transferTo(savePath.toFile());

            String imageUrl = "/uploads/" + filename;
            return Map.of("url", imageUrl);

        } catch (Exception e) {
            // ❗ 반드시 JSON으로 에러 응답해야 클라이언트가 파싱 오류 안 남
            return Map.of("error", "서버 내부 오류 발생: " + e.getMessage());
        }
    }
}
