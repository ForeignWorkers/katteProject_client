package me.soldesk.katte_project_client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SupportController {

    //고객센터 공지사항(디폴트)으로 이동
    @GetMapping("/support")
    public String support() {
        return "redirect:/html/Support.html"; // 추후 등록
    }
}
