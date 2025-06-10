package me.soldesk.katte_project_client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    //메인 페이지로 이동(사이트 접속 시 최초 페이지)
    @GetMapping("/")
    public String home() {
        return "redirect:/html/Mainpage/Mainpage.html";
    }

    //메인 페이지로 이동(로고 선택 시)
    @GetMapping("/main")
    public String mainpage() {
        return "redirect:/html/Mainpage/Mainpage.html";
    }

    //로그인 페이지로 이동
    @GetMapping("/login")
    public String login() {
        return "redirect:/html/Loginpage/Login.html";
    }
}
