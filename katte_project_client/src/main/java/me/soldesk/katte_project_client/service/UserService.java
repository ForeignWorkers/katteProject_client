package me.soldesk.katte_project_client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import common.bean.ecommerce.EcommerceCoupon;
import common.bean.ecommerce.EcommerceCouponHistory;
import common.bean.user.UserBean;
import common.bean.user.UserKatteMoneyLogBean;
import common.bean.user.UserPaymentBean;
import me.soldesk.katte_project_client.manager.ApiManagers;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
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

    public List<EcommerceCouponHistory> getUserCouponHistory(int user_id) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("user_id", Integer.toString(user_id));

        TypeReference<List<EcommerceCouponHistory>> ref = new TypeReference<>() {};
        ResponseEntity<List<EcommerceCouponHistory>> result = ApiManagers.get("coupon/user", requestBody, ref);
        return result.getBody();
    }

    public EcommerceCoupon getUserCoupon(int coupon_id) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("coupon_id", Integer.toString(coupon_id));
        TypeReference<EcommerceCoupon> ref = new TypeReference<>() {};
        ResponseEntity<EcommerceCoupon> result = ApiManagers.get("coupon/data", requestBody, ref);
        return result.getBody();
    }

    public String setPoint(int user_id, int point) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("user_id", Integer.toString(user_id));
        requestBody.put("point", Integer.toString(point));
        TypeReference<String> ref = new TypeReference<>() {};
        ResponseEntity<String> result = ApiManagers.patchQuery("user/point", requestBody, ref);
        return result.getBody();
    }

    public String useKatteMoney(UserKatteMoneyLogBean userKatteMoneyLogBean){
        TypeReference<String> ref = new TypeReference<>() {};
        ResponseEntity<String> result = ApiManagers.patchBody("user/katte", userKatteMoneyLogBean, ref);
        return result.getBody();
    }
}
