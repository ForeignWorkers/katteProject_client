package me.soldesk.katte_project_client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StyleController {

    //스타일 페이지 이동
    @GetMapping("/style")
    public String style() {
        return "redirect:/html/Stylepage/Stylepage.html";
    }

    //스타일 -> 특정 게시 스타일 페이지 이동
    @GetMapping("/style/detail")
    public String styleDetail() {
        return "redirect:/html/Stylepage/Style_detail.html";
    }
}