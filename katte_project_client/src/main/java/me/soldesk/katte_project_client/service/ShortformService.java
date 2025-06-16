package me.soldesk.katte_project_client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import common.bean.content.ContentShortformBean;
import common.bean.product.ProductPerSaleBean;
import me.soldesk.katte_project_client.manager.ApiManagers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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
}
