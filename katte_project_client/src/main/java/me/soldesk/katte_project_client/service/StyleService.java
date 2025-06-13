package me.soldesk.katte_project_client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.bean.content.ContentStyleBean;
import me.soldesk.katte_project_client.manager.ApiManagers;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Arrays;
import java.util.List;

@Service
public class StyleService {


    /*private final RestTemplate restTemplate = new RestTemplate();*/

    /**
     * @param style_title 제목
     * @param imgCount    실제 업로드된 이미지 개수
     * @param images      MultipartFile 배열 (나중에 이미지 API에 그대로 전달용)
     * @param caption     캡션
     * @param productTag  상품 태그
     * @param hashtags    해시태그 리스트 (컨트롤러에서 JSON → List로 파싱 후 전달)
     */
    public void save(
            String style_title,
            int imgCount,
            MultipartFile[] images,
            String caption,
            String productTag,
            List<String> hashtags,
            int user_id) {

        // 1) ContentStyleBean 세팅
        ContentStyleBean style = new ContentStyleBean();
        style.setStyle_title(style_title);
        style.setCaption(caption);
        style.setUser_id(user_id);          // TODO: 실제 로그인 유저 ID로 교체
        style.setImg_count(imgCount);
        style.setHashtags(hashtags);
        TypeReference<Integer> ref = new TypeReference<Integer>() {};

        // ✅ 객체 자체를 바디로 전송
        ResponseEntity<Integer> result = ApiManagers.post(
                "content/style",
                style,
                ref
        );

        System.out.println("images size " + images.length);

        try {
            for(MultipartFile item: images){
                saveStyleImage(result.getBody(),item);
            }
        }
        catch (Exception e) {
            System.out.println("이미지 저장 실패 " + e.getMessage());
        }

        if(ApiManagers.isSuccessful(result)) {
            System.out.printf("Successfully uploaded style %s\n", result.getBody());
        }
        else  {
            System.out.printf("Failed to upload style %s\n", result.getBody());
        }
    }


    // 저장할 실제 파일 시스템 경로 (ngrok 으로 매핑된 폴더)
    private static final String STYLE_IMAGE_DIR = "https://resources-katte.jp.ngrok.io/images/style/";

    /**
     * style/{styleId}/image 로 받은 단일 파일을
     * STYLE_IMAGE_DIR/style_{styleId}.png 로 저장
     */
    public void saveStyleImage(int styleId, MultipartFile file) throws IOException, IOException {
        // 1) 디렉토리 없으면 생성
        Path dir = Paths.get(STYLE_IMAGE_DIR);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        // 2) 확장자 추출 (원본 파일명 예: foo.png → png)
        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.') + 1);
        }

        // 3) 저장할 파일명 (항상 .png 확장자로 통일하고 싶으면 ext 대신 "png" 고정)
        String filename = "style_" + styleId + (ext.isEmpty() ? "" : "." + ext);

        // 4) 실제 저장
        Path target = dir.resolve(filename);
        file.transferTo(target.toFile());
    }
}