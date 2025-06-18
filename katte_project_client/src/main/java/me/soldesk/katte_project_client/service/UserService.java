package me.soldesk.katte_project_client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import common.bean.user.UserBean;
import common.bean.user.UserPaymentBean;
import me.soldesk.katte_project_client.manager.ApiManagers;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    public UserBean getUserByEmail(String email) {
            // ✅ 바디 구성 (Map 또는 DTO 가능)
            Map<String, String> requestBody = new HashMap<>();

            requestBody.put("user_id", null);
            requestBody.put("email_id", email);
        return getUserBean(requestBody);
    }

    public UserBean getUserByUserId(Integer user_id) {
        // ✅ 바디 구성 (Map 또는 DTO 가능)
        Map<String, String> requestBody = new HashMap<>();

        requestBody.put("user_id", Integer.toString(user_id));
        requestBody.put("email_id", null);
        return getUserBean(requestBody);
    }

    @Nullable
    private UserBean getUserBean(Map<String, String> requestBody) {
        TypeReference<UserBean> ref = new TypeReference<>() {};

        ResponseEntity<UserBean> result = ApiManagers.get(
                "user",
                requestBody,
                ref
        );

        if (ApiManagers.isSuccessful(result)) {
            System.out.println("유저 찾기 성공");
        } else {
            System.out.println("유저 찾기 실패");
        }
        return result.getBody();
    }

    public UserPaymentBean getUserPayment(int user_id) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("user_id", Integer.toString(user_id));

        TypeReference<UserPaymentBean> ref = new TypeReference<>() {};
        ResponseEntity<UserPaymentBean> result = ApiManagers.get("user/payment", requestBody, ref);

        return result.getBody();
    }
}
