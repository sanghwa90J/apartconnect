package com.aptconnect.menu.complaint.service;

import com.aptconnect.menu.complaint.entity.Complaint;
import com.aptconnect.menu.complaint.entity.ComplaintFile;
import com.aptconnect.menu.complaint.repository.ComplaintFileRepository;
import com.aptconnect.menu.complaint.repository.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintFileService {
    private final ComplaintFileRepository fileRepository;
    private final ComplaintRepository complaintRepository;

    private static final String UPLOAD_DIR = "C:/uploads/"; // 또는 "/home/user/uploads/"

    // ✅ 파일 저장
    public void saveFile(Long complaintId, MultipartFile file) throws IOException {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("해당 민원을 찾을 수 없습니다."));

        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR + fileName);

        // 폴더 없으면 생성
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        // 파일 저장
        Files.copy(file.getInputStream(), filePath);

        // DB에 저장
        ComplaintFile complaintFile = ComplaintFile.builder()
                .fileName(fileName)
                .filePath(filePath.toString())
                .fileType(file.getContentType())
                .complaint(complaint)
                .build();

        fileRepository.save(complaintFile);
    }

    // ✅ 특정 민원에 대한 파일 리스트 가져오기
    public List<ComplaintFile> getFilesByComplaint(Long complaintId) {
        return fileRepository.findByComplaintId(complaintId);
    }

    public ComplaintFile getFileById(Long fileId) {
        return fileRepository.findById(fileId).orElseThrow(() -> new RuntimeException("해당 파일을 찾을 수 없습니다."));

    }

    public void saveFiles(Complaint complaint, MultipartFile[] files) {
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    // ✅ 저장할 디렉토리 생성 (없을 경우 자동 생성)
                    Path uploadPath = Paths.get(UPLOAD_DIR);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    // ✅ 파일 저장 경로 설정
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    Path filePath = uploadPath.resolve(fileName);

                    // ✅ 파일 저장
                    file.transferTo(filePath.toFile());

                    // ✅ DB 저장
                    ComplaintFile complaintFile = new ComplaintFile();
                    complaintFile.setComplaint(complaint);
                    complaintFile.setFileName(file.getOriginalFilename());
                    complaintFile.setFilePath(filePath.toString());
                    complaintFile.setFileType(file.getContentType());

                    fileRepository.save(complaintFile);
                } catch (IOException e) {
                    e.printStackTrace(); // ✅ 예외 로그 출력
                    throw new RuntimeException("파일 업로드 실패", e);
                }
            }
        }
    }
}