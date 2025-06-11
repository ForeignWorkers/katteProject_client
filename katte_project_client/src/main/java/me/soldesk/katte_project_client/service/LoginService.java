package me.soldesk.katte_project_client.service;

import common.bean.user.UserBean;
import me.soldesk.katte_project_client.manager.ApiManagers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginService {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public boolean signUp(UserBean userBean){
        // 비밀번호 암호화 예시
        String encoded = passwordEncoder.encode(userBean.getPassword());
        userBean.setPassword(encoded); // 암호화된 비밀번호로 덮어쓰기

        // ✅ 객체 자체를 바디로 전송
        ResponseEntity<String> result = ApiManagers.post(
                "user",
                userBean,
                String.class
        );

        if (ApiManagers.isSuccessful(result)) {
            System.out.println("회원 가입 성공!");
            return true;
        } else {
            System.out.println("회원 가입 실패!");
            return false;
        }
    }

    public boolean isLogin(String email, String password) {
        // ✅ 바디 구성 (Map 또는 DTO 가능)
        Map<String, String> requestBody = new HashMap<>();

        String encoded = encodePassword(password);

        requestBody.put("email_id", email);
        requestBody.put("password", encoded);


        // ✅ POST 요청 실행
        ResponseEntity<Boolean> result = ApiManagers.post(
                "user/login",
                requestBody,
                Boolean.class
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

        // ✅ 객체 자체를 바디로 전송
        ResponseEntity<String> result = ApiManagers.post(
                "/user/terms",
                requestBody,
                String.class
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
}
