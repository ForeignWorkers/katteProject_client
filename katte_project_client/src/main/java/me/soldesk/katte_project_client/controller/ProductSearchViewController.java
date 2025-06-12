package me.soldesk.katte_project_client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductSearchViewController {

    @GetMapping("/product-search-popup")
    public String showProductSearchPopup() {

        return "Serachpage/product_search_popup";  // templates/product/Product_search_popup.html
    }


}