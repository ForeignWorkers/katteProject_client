package me.soldesk.katte_project_client.Advice;

import com.fasterxml.jackson.core.type.TypeReference;
import common.bean.user.UserBean;
import common.bean.user.UserPaymentBean;
import jakarta.servlet.http.HttpSession;
import me.soldesk.katte_project_client.manager.ApiManagers;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import java.util.Map;

@ControllerAdvice
public class GlobalLoginInfoAdvice {

    @ModelAttribute
    public void addLoginInfo(Model model, HttpSession session) {
        System.out.println("GlobalLoginInfoAdvice 실행됨");

        Object currentUserObj = session.getAttribute("currentUser");

        if (currentUserObj instanceof UserBean user) {
            String userId = String.valueOf(user.getUser_id());
            String email = user.getEmail_id();

            System.out.println(" userId = " + userId);
            System.out.println(" email = " + email);

            try {
                TypeReference<UserBean> ref = new TypeReference<>() {};

                // 최신 유저 정보 조회 (선택: 생략 가능)
                UserBean refreshedUser = ApiManagers.get("user", Map.of(
                        "user_id", userId,
                        "email_id", email
                ), ref).getBody();

                TypeReference<UserPaymentBean> refPay = new TypeReference<>() {};

                UserPaymentBean payment = ApiManagers.get("user/payment", Map.of(
                        "user_id", userId
                ), refPay).getBody();

                if (refreshedUser != null) {
                    model.addAttribute("nickname", refreshedUser.getNickname());
                    model.addAttribute("isAdmin", refreshedUser.getIs_admin());
                }

                if (payment != null) {
                    model.addAttribute("katteMoney", payment.getKatte_money());
                    model.addAttribute("point", payment.getPoint());
                }

            } catch (Exception e) {
                System.err.println("GlobalLoginInfoAdvice 예외 발생: " + e.getMessage());
            }
        }
    }
}
