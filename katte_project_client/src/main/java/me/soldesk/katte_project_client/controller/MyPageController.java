package me.soldesk.katte_project_client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyPageController {

    //마이 페이지 구매내역(디폴트)으로 이동
    @GetMapping("/mypage")
    public String myPage() {
        return "redirect:/html/CmMyPage/MyPage.html";
    }

    //마이페이지 관심상품 페이지로 이동
    @GetMapping("/favorite")
    public String favorite() {
        return "redirect:/html/Favorite.html";
    }

}
