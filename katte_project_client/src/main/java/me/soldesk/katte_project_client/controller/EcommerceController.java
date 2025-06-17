package me.soldesk.katte_project_client.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EcommerceController {

    @GetMapping("/mypage/kattemoney")
    public String moveToKatteMoneyPage(HttpSession session, Model model) {
        // 로그인 여부 체크 등 필요한 데이터 주입 가능
        return "CsMyPage/Mypage_kattemoney_charge";
    }
}
