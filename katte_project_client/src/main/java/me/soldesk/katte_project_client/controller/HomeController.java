package me.soldesk.katte_project_client.controller;

import common.bean.user.UserBean;
import common.bean.user.UserPaymentBean;
import jakarta.servlet.http.HttpSession;
import me.soldesk.katte_project_client.manager.ApiManagers;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class HomeController {

    //메인 페이지로 이동(사이트 접속 시 최초 페이지)
    @GetMapping("/")
    public String home() {
        return "redirect:/main";
    }

    // 메인 페이지 이동 (로고 클릭 포함)
    @GetMapping("/main")
    public String mainPage(HttpSession session, Model model) {

        //세션 test용
        //session.setAttribute("userId", 3);
        //session.setAttribute("email", "admin@example.com");

        UserBean userBean = (UserBean) session.getAttribute("currentUser");

        if (userBean != null) {
            System.out.println("⚠️ 로그인 세션 있으면");

            Map<String, String> requestBody = new HashMap<>();

            requestBody.put("user_id", Integer.toString(userBean.getUser_id()));
            TypeReference<UserPaymentBean> ref = new TypeReference<>(){};

            UserPaymentBean payment = ApiManagers.get("user/payment",
                    requestBody,
                                ref).getBody();

            model.addAttribute("nickname", userBean.getNickname());
            model.addAttribute("katteMoney", payment.getKatte_money());
            model.addAttribute("point", payment.getPoint());
            model.addAttribute("isAdmin", userBean.getIs_admin());
        }

        return "Mainpage/Mainpage";
    }
}
