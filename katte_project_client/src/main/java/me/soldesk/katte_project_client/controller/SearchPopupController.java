package me.soldesk.katte_project_client.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchPopupController {

    @GetMapping("/product-search-popup")
    public String showPopup(HttpServletRequest request) {
        String referer = request.getHeader("Referer");

        // 허용된 경로에서만 접근 가능하도록 (스타일 등록, 상품 등록)
        if (referer != null && (referer.contains("/styleupload") || referer.contains("/sell"))) {
            return "Serachpage/Product_search_popup";
        }

        return "error/403"; //접근 차단용 에러 페이지
    }
}
