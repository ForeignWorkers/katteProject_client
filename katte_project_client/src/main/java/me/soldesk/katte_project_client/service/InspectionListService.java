package me.soldesk.katte_project_client.service;

import com.fasterxml.jackson.core.type.TypeReference;

import me.soldesk.katte_project_client.manager.ApiManagers;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import common.bean.admin.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InspectionListService {

    public List<InspectionProductViewBean> getInspectionItems(int page, int size) {
        Map<String, String> params = new HashMap<>();
        params.put("offset", String.valueOf((page - 1) * size));
        params.put("size", String.valueOf(size));

        ResponseEntity<List<InspectionProductViewBean>> res =
                ApiManagers.get("admin/inspection", params, new TypeReference<>() {});

        System.out.println("API 결과: " + res.getStatusCode());
        System.out.println("Body: " + res.getBody());

        List<InspectionProductViewBean> body = res.getBody();

        //방어코드 추가
        return body != null ? body : new ArrayList<>();
    }

    public int getInspectionTotalCount() {
        ResponseEntity<Integer> res =
                ApiManagers.get("admin/inspection/count", null, new TypeReference<>() {});
        return res.getBody() != null ? res.getBody() : 0;
    }

}