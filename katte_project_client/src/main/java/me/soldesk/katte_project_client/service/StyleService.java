package me.soldesk.katte_project_client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.bean.content.ContentStyleBean;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class StyleService {

    private final RestTemplate restTemplate = new RestTemplate(); // ✅ HTTP 요청용

    public void save(String style_title, MultipartFile[] images, String caption, String productTag, String hashtagsJson) {
        ContentStyleBean style = new ContentStyleBean();
        style.setStyle_title(style_title);
        style.setCaption(caption); // ✅ caption 저장
        style.setUser_id(1); // TODO: 로그인 유저 ID로 대체 필요
        style.setImg_count(images.length); // 이미지 개수

        // ✅ 해시태그 JSON 처리
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<String> hashtags = objectMapper.readValue(hashtagsJson, new TypeReference<>() {});
            style.setHashtags(hashtags);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ✅ HTTP 요청 구성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ContentStyleBean> request = new HttpEntity<>(style, headers);

        // ✅ 백엔드 서버로 POST 요청 보내기 (9000포트 기준)
        try {
            restTemplate.postForEntity("http://localhost:9000/content/style", request, String.class);
        } catch (Exception e) {
            e.printStackTrace(); // 실패 로그
        }

        // 👉 이미지 업로드는 별도 API로 전송하거나, 나중에 multipart 연동 추가
    }
}
