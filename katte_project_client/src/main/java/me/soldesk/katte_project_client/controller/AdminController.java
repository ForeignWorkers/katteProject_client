package me.soldesk.katte_project_client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    //관리자 회원 관리 페이지(디폴트)로 이동
    @GetMapping("/admin")
    public String adminMain() {
        return "redirect:/html/Admin/Admin.html";
    }
}
