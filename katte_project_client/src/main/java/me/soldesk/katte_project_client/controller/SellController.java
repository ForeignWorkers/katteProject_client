package me.soldesk.katte_project_client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SellController {

    //판매 페이지 판매 등록(디폴트)으로 이동
    @GetMapping("/sell")
    public String sell() {
        return "redirect:/html/Sell.html"; // 추후 등록
    }
}