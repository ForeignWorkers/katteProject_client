package me.soldesk.katte_project_client.manager;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
public class UploadVideo {

    @PostMapping("/upload/video")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        try {
            if (!file.getContentType().startsWith("video/")) {
                return ResponseEntity.badRequest().body("비디오 파일만 가능합니다.");
            }

            // 저장 디렉토리 (절대 경로 or 상대 경로)

            String uploadDir = "src/main/resources/static/uploads/videos/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            // 파일명 생성
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(uploadDir + fileName);
            file.transferTo(dest);

            // 추후 URL 형태로 접근 가능
            String fileUrl = "/uploads/videos/" + fileName;
            return ResponseEntity.ok(fileUrl);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("파일 업로드 실패");
        }
    }

}
