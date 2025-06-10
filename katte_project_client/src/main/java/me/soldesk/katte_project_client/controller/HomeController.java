package me.soldesk.katte_project_client.controller;

import common.bean.user.UserBean;
import common.bean.user.UserPaymentBean;
import jakarta.servlet.http.HttpSession;
import me.soldesk.katte_project_client.manager.ApiManagers;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class HomeController {

    //메인 페이지로 이동(사이트 접속 시 최초 페이지)
    @GetMapping("/")
    public String home() {
        return "Mainpage/Mainpage";
    }

    // 메인 페이지 이동 (로고 클릭 포함)
    @GetMapping("/main")
    public String mainPage(HttpSession session, Model model) {

        //세션 test용
        session.setAttribute("userId", 3);
        session.setAttribute("email", "admin@example.com");

        String userId = String.valueOf(session.getAttribute("userId"));
        String email = (String) session.getAttribute("email");

        if (userId != null && email != null) {
            UserBean user = ApiManagers.get("/user", Map.of(
                    "user_id", userId,
                    "email_id", email
            ), UserBean.class).getBody();

            UserPaymentBean payment = ApiManagers.get("/user/payment", Map.of(
                    "user_id", userId
            ), UserPaymentBean.class).getBody();

            model.addAttribute("nickname", user.getNickname());
            model.addAttribute("katteMoney", payment.getKatte_money());
            model.addAttribute("point", payment.getPoint());
            model.addAttribute("isAdmin", user.getIs_admin());
        }

        return "Mainpage/Mainpage";
    }

    //로그인 페이지로 이동
    @GetMapping("/login")
    public String login() {
        return "Loginpage/Login";
    }


}
