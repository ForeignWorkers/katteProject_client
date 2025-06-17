package me.soldesk.katte_project_client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import common.bean.admin.RegisteredProductViewBean;
import common.bean.content.ContentStyleBean;
import common.bean.product.ProductInfoBean;
import me.soldesk.katte_project_client.manager.ApiManagers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    //제품 아이템 검색
    public List<ProductInfoBean>getProductInfoKeyword(String keyword) {
        Map<String, String> params = new HashMap<>();
        params.put("type", "PRODUCT");
        params.put("keyword", keyword);

        ResponseEntity<List<ProductInfoBean>> res =
                ApiManagers.get("search", params, new TypeReference<>() {});
        return res.getBody();
    }

    //스타일 검색
    public List<ContentStyleBean>getStyleInfoKeyword(String keyword) {
        Map<String, String> params = new HashMap<>();
        params.put("type", "STYLE");
        params.put("keyword", keyword);

        ResponseEntity<List<ContentStyleBean>> res =
                ApiManagers.get("search", params, new TypeReference<>() {});
        return res.getBody();
    }
}
