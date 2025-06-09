package me.soldesk.katte_project_client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {

    //검색 페이지 이동
    @GetMapping("/search")
    public String search() {
        return "redirect:/html/Search.html";
    }

    //탐색 페이지 이동
    @GetMapping("/explore")
    public String explore() {
        return "redirect:/html/Explore.html"; // 추후 추가
    }
}
