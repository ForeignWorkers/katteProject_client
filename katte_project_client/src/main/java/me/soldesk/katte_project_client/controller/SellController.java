package me.soldesk.katte_project_client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SellController {

    //판매 페이지 판매 등록(디폴트)으로 이동
    @GetMapping("/sell")
    public String sell() {
        return "Sell_product_upload/Sell_product_upload";
    }

    //상품 등록 탭 누를 시 페이지 이동
    @GetMapping("/sell/registration")
    public String sellRegistration() {
        return "Sell_product_upload/Sell_product_upload";
    }

    //등록 상품 관리 탭 누를 시 페이지로 이동
    @GetMapping("/sell/manage")
    public String sellManage() {
        return "Sell_product_management/Sell_product_management_empty";
    }


}