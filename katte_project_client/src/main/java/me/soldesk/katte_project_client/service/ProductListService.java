package me.soldesk.katte_project_client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import me.soldesk.katte_project_client.manager.ApiManagers;

import common.bean.admin.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductListService {

    //등록 상품 리스트
    public List<RegisteredProductViewBean> getRegisteredItems(int page, int size) {
        Map<String, String> params = new HashMap<>();
        params.put("offset", String.valueOf((page - 1) * size));
        params.put("size", String.valueOf(size));

        ResponseEntity<List<RegisteredProductViewBean>> res =
                ApiManagers.get("products/registered", params, new TypeReference<>() {});
        return res.getBody();
    }

    //등록 상품 수
    public int getRegisteredCount() {
        ResponseEntity<Integer> res =
                ApiManagers.get("products/registered/count", null, new TypeReference<>() {});
        return res.getBody() != null ? res.getBody() : 0;
    }

    //판매 완료 리스트
    public List<SoldoutProductViewBean> getSoldoutItems(int page, int size) {
        Map<String, String> params = new HashMap<>();
        params.put("offset", String.valueOf((page - 1) * size));
        params.put("size", String.valueOf(size));

        ResponseEntity<List<SoldoutProductViewBean>> res =
                ApiManagers.get("products/soldout", params, new TypeReference<>() {});
        return res.getBody();
    }

    //판매 완료 수
    public int getSoldoutCount() {
        ResponseEntity<Integer> res =
                ApiManagers.get("products/soldout/count", null, new TypeReference<>() {});
        return res.getBody() != null ? res.getBody() : 0;
    }

    public String settleAuction(int auctionId) {
        try {
            ResponseEntity<String> response = ApiManagers.post(
                    "settlement?auction_id=" + auctionId, null, new TypeReference<String>() {}
            );

            if (ApiManagers.isSuccessful(response)) {
                return response.getBody();
            } else {
                return "정산 요청 실패: " + response.getBody();
            }
        } catch (Exception e) {
            return "정산 중 오류 발생: " + e.getMessage();
        }
    }

}
