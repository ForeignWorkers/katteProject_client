package me.soldesk.katte_project_client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import common.bean.user.UserBean;
import me.soldesk.katte_project_client.manager.ApiManagers;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginService {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    @Value("${naver.redirect.url}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();

    public UserBean signUp(UserBean userBean){
        // 비밀번호 암호화 예시
        String encoded = encodePassword(userBean.getPassword());
        userBean.setPassword(encoded); // 암호화된 비밀번호로 덮어쓰기
        TypeReference<UserBean> ref = new TypeReference<>() {};

        // ✅ 객체 자체를 바디로 전송
        ResponseEntity<UserBean> result = ApiManagers.post(
                "user",
                userBean,
                ref
        );

        if (ApiManagers.isSuccessful(result)) {
            System.out.println("회원 가입 성공!");
            return result.getBody();
        } else {
            System.out.println("회원 가입 실패!");
            return result.getBody();
        }
    }

    public boolean isLogin(String email, String rawPassword) {
        // 평문 비밀번호 전송 (❗주의: HTTPS로 통신해야 안전)
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email_id", email);
        requestBody.put("password", rawPassword);
        TypeReference<Boolean> ref = new TypeReference<>() {};

        // POST 요청 실행
        ResponseEntity<Boolean> result = ApiManagers.post(
                "user/login",
                requestBody,
                ref
        );

        if (ApiManagers.isSuccessful(result)) {
            System.out.println("통신 성공!");
            return Boolean.TRUE.equals(result.getBody());
        } else {
            System.out.println("통신 실패!");
            return false;
        }
    }

    public boolean addUserTerm(String user_id, int term_code, boolean is_agreed){

        // ✅ 바디 구성 (Map 또는 DTO 가능)
        Map<String, String> requestBody = new HashMap<>();

        requestBody.put("user_id", user_id);
        requestBody.put("term_code", String.valueOf(term_code));
        requestBody.put("is_agreed", String.valueOf(is_agreed));
        TypeReference<String> ref = new TypeReference<>() {};

        // ✅ 객체 자체를 바디로 전송
        ResponseEntity<String> result = ApiManagers.post(
                "/user/terms",
                requestBody,
                ref
        );

        if (ApiManagers.isSuccessful(result)) {
            System.out.println("약관 등록 성공!");
            return true;
        } else {
            System.out.println("약관 등록 실패!");
            return false;
        }
    }

    // ✅ 비밀번호 비교
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // ✅ 비밀번호 암호화
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public NaverUserDTO getUserProfile(String code, String state) {

        // 1. Access Token 요청
        String tokenUrl = "https://nid.naver.com/oauth2.0/token"
                + "?grant_type=authorization_code"
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&redirect_uri=" + redirectUri
                + "&code=" + code
                + "&state=" + state;

        ResponseEntity<Map> tokenRes = restTemplate.getForEntity(tokenUrl, Map.class);
        String accessToken = (String) tokenRes.getBody().get("access_token");

        // 2. 사용자 정보 요청
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> userRes = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                entity,
                Map.class
        );

        Map<String, Object> response = (Map<String, Object>) userRes.getBody().get("response");

        System.out.println(response);
        // 3. DTO 변환
        NaverUserDTO user = new NaverUserDTO();
        user.setEmail((String) response.get("email"));
        user.setNickname((String) response.get("nickname"));
        user.setName((String) response.get("name"));
        user.setId((String) response.get("id"));
        user.setPhoneNumber((String) response.get("mobile"));
        user.setBirthday((String) response.get("birthday"));
        user.setBirthyear((String) response.get("birthyear"));

        return user;
    }
}
