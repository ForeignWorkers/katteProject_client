package me.soldesk.katte_project_client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import common.bean.content.ContentShortformBean;
import common.bean.ecommerce.EcommerceTradeLookUp;
import common.bean.product.ProductPerSaleBean;
import me.soldesk.katte_project_client.manager.ApiManagers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShortformService {

    public ContentShortformBean getRandomShort() {
        TypeReference<ContentShortformBean> ref = new TypeReference<>() {};
        ResponseEntity<ContentShortformBean> response = ApiManagers.get("content/short/one_random",null,ref);
        return response.getBody();
    }

    public boolean toggleLike(int user_id, int short_id) {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", Integer.toString(user_id));
        params.put("short_id", Integer.toString(short_id));

        TypeReference<Boolean> ref = new TypeReference<>() {};
        ResponseEntity<Boolean> response = ApiManagers.patchQuery("content/short/like" ,params ,ref);
        return Boolean.TRUE.equals(response.getBody());
    }

    public ProductPerSaleBean getProductPerSaleUseShortId(int short_id) {
        Map<String, String> params = new HashMap<>();
        params.put("short_id", Integer.toString(short_id));
        TypeReference<ProductPerSaleBean> ref = new TypeReference<>() {};

        ResponseEntity<ProductPerSaleBean> res = ApiManagers.get("content/short/product_per_sale",params,ref);
        return res.getBody();
    }

    public List<EcommerceTradeLookUp> getTrades(String productId, String range) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fromDate;

        switch (range) {
            case "1m": fromDate = now.minusMonths(1); break;
            case "3m": fromDate = now.minusMonths(3); break;
            case "6m": fromDate = now.minusMonths(6); break;
            case "1y": fromDate = now.minusYears(1); break;
            default: fromDate = now.minusMonths(1); break;
        }
        // ⏱ ISO 포맷 문자열 (예: 2025-05-01T00:00:00)
        String fromDateStr = fromDate.toString();

        Map<String, String> params = new HashMap<>();
        params.put("product_id", String.valueOf(productId));
        params.put("fromDate", fromDateStr);

        // 응답 타입 명시
        TypeReference<List<EcommerceTradeLookUp>> ref = new TypeReference<>() {};

        // ApiManagers는 커스텀 유틸이라고 가정
        ResponseEntity<List<EcommerceTradeLookUp>> res =
                ApiManagers.get("ecommerce/trade", params, ref);

        System.out.println("TTTTTTTT" + res.getBody());
        return res.getBody();
    }
}
