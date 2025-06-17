package me.soldesk.katte_project_client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import common.bean.content.ContentStyleBean;
import common.bean.content.ContentStyleProductJoinBean;
import me.soldesk.katte_project_client.manager.ApiManagers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ContentService {
    public List<ContentStyleProductJoinBean> getContentStyleProducts(String product_id) {
        Map<String, String> params = new HashMap<>();
        params.put("product_id", product_id);

        TypeReference<List<ContentStyleProductJoinBean>> ref = new TypeReference<>() {};

        ResponseEntity<List<ContentStyleProductJoinBean>> result =
                ApiManagers.get("content/styleProductJoin", params, ref);

        return result.getBody();
    }

    public ContentStyleBean getContentStyle(int style_id) {
        Map<String, String> params = new HashMap<>();
        params.put("style_id", Integer.toString(style_id));

        TypeReference<ContentStyleBean> ref = new TypeReference<>() {};

        ResponseEntity<ContentStyleBean> result = ApiManagers.get("content/style", params, ref);

        return result.getBody();
    }
}
