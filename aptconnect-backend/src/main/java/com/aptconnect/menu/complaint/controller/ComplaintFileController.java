package com.aptconnect.menu.complaint.controller;

import com.aptconnect.menu.complaint.entity.ComplaintFile;
import com.aptconnect.menu.complaint.service.ComplaintFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/complaints/{complaintId}/files")
@RequiredArgsConstructor
public class ComplaintFileController {
    private final ComplaintFileService fileService;

    // ✅ 파일 업로드 API
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @PathVariable Long complaintId,
            @RequestParam("file") MultipartFile file) {

        try {
            fileService.saveFile(complaintId, file);
            return ResponseEntity.ok("파일 업로드 성공");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("파일 업로드 실패: " + e.getMessage());
        }
    }

    // ✅ 특정 민원의 파일 목록 조회
    @GetMapping
    public ResponseEntity<List<ComplaintFile>> getComplaintFiles(@PathVariable Long complaintId) {
        return ResponseEntity.ok(fileService.getFilesByComplaint(complaintId));
    }

    // ✅ 파일 다운로드 API (파일을 강제로 다운로드하도록 설정)
    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) throws IOException {
        ComplaintFile file = fileService.getFileById(fileId);
        Path filePath = Paths.get(file.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());

        // 🔥 파일명을 UTF-8로 인코딩
        String encodedFileName = URLEncoder.encode(file.getFileName(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20"); // 공백 처리

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                .body(resource);
    }
}