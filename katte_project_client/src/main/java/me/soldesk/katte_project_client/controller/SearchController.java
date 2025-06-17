package me.soldesk.katte_project_client.controller;

import common.bean.content.ContentStyleBean;
import common.bean.product.ProductInfoBean;
import me.soldesk.katte_project_client.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SearchController {
    @Autowired
    SearchService searchService;

    //검색 페이지 이동
    @GetMapping("/search")
    public String search() {
        return "Serachpage/Search";
    }

    //데이터 검색
    @GetMapping("/search/product")
    @ResponseBody
    public List<ProductInfoBean> searchProduct(@RequestParam("keyword") String keyword) {
        return searchService.getProductInfoKeyword(keyword);
    }

    //데이터 검색
    @GetMapping("/search/style")
    @ResponseBody
    public List<ContentStyleBean> searchStyle(@RequestParam("keyword") String keyword) {
        return searchService.getStyleInfoKeyword(keyword);
    }
}
