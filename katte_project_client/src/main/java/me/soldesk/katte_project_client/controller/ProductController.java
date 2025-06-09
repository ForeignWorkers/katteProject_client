package me.soldesk.katte_project_client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductController {

    //특정 상품 디테일 페이지 이동
    @GetMapping("/product/detail")
    public String productDetail() {
        return "redirect:/html/ProductDetail.html";
    }
}
