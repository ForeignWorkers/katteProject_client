package me.soldesk.katte_project_client.service;

import common.bean.auction.AuctionDataBean;
import common.bean.ecommerce.EcommerceOrderBean;
import common.bean.product.*;
import common.bean.user.UserPaymentBean;
import me.soldesk.katte_project_client.manager.ApiManagers;
import common.bean.product.ProductPriceSummaryBean;
import common.bean.product.ProductSizeWithSellPriceBean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    // 상품 정보 가져오기
    public ProductInfoBean getProductInfo(int productId) {
        Map<String, String> params = Map.of("product_id", String.valueOf(productId));
        ResponseEntity<ProductInfoBean> response =
                ApiManagers.get("product", params, new TypeReference<>() {});
        return getOrThrow(response, "상품 정보를 불러올 수 없습니다.");
    }

    public ProductPriceSummaryBean getProductPriceSummary(int productId) {
        Map<String, String> params = Map.of("product_id", String.valueOf(productId));
        TypeReference<ProductPriceSummaryBean> typeRef = new TypeReference<>() {};
        ResponseEntity<ProductPriceSummaryBean> response =
                ApiManagers.get("product/price_summary", params, typeRef);

        System.out.println("✔️ 응답 성공 여부: " + ApiManagers.isSuccessful(response));
        System.out.println("✔️ 응답 바디: " + response.getBody());
        System.out.println("👉 가격 요약 요청 결과: " + response.getBody());

        if (ApiManagers.isSuccessful(response) && response.getBody() != null) {
            return response.getBody();
        } else {
            return new ProductPriceSummaryBean(); // 혹은 예외 던지기
        }
    }

    public Integer getLowestSellPrice(int productId) {
        Map<String, String> params = Map.of("product_id", String.valueOf(productId));
        ResponseEntity<Integer> response = ApiManagers.get(
                "auction/lowest_sell_price",
                params,
                new TypeReference<>() {}
        );

        System.out.println("🔥 즉시 판매가 응답: " + response); // 추가
        System.out.println("🔥 응답 바디: " + response.getBody()); // 추가

        if (ApiManagers.isSuccessful(response) && response.getBody() != null) {
            return response.getBody();
        } else {
            return null;
        }
    }

    // 사이즈별 최저 즉시 판매가 조회
    public List<ProductSizeWithSellPriceBean> getLowestSellPriceBySize(int productId) {
        Map<String, String> params = Map.of("product_id", String.valueOf(productId));
        TypeReference<List<ProductSizeWithSellPriceBean>> ref = new TypeReference<>() {};
        ResponseEntity<List<ProductSizeWithSellPriceBean>> response =
                ApiManagers.get("auction/lowest_sell_price/size", params, ref);

        if (ApiManagers.isSuccessful(response) && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("사이즈별 최저 즉시 판매가를 가져오는데 실패했습니다");
        }
    }

    // 시세표관련
    public List<Map<String, Object>> getPriceHistory(int productId, String period) {
        Map<String, String> params = new HashMap<>();
        params.put("product_id", String.valueOf(productId));
        params.put("period", period); // "1M", "3M", ...

        TypeReference<List<Map<String, Object>>> ref = new TypeReference<>() {};
        ResponseEntity<List<Map<String, Object>>> response = ApiManagers.get("product/price_history", params, ref);

        if (ApiManagers.isSuccessful(response)) {
            return response.getBody();
        } else {
            throw new RuntimeException("시세 데이터를 불러오는 데 실패했습니다.");
        }
    }

    public List<ProductSizeWithPriceBean> getSizePriceList(int productId) {
        Map<String, String> params = Map.of("product_id", String.valueOf(productId));
        TypeReference<List<ProductSizeWithPriceBean>> ref = new TypeReference<>() {};
        ResponseEntity<List<ProductSizeWithPriceBean>> response = ApiManagers.get("product/size_prices", params, ref);

        if (ApiManagers.isSuccessful(response) && response.getBody() != null) {
            List<ProductSizeWithPriceBean> result = response.getBody();
            System.out.println("📦 [DEBUG] 받아온 사이즈/즉시가 개수: " + result.size());
            for (ProductSizeWithPriceBean item : result) {
                System.out.println("🧩 size=" + item.getAuction_size_value() + ", price=" + item.getPrice());
            }
            return result;
        }
        return Collections.emptyList();
    }

    public List<ProductSizeWithSellPriceBean> getSizePriceListByProductId(int productId) {
        Map<String, String> params = Map.of("product_id", String.valueOf(productId));
        TypeReference<List<ProductSizeWithSellPriceBean>> ref = new TypeReference<>() {};
        ResponseEntity<List<ProductSizeWithSellPriceBean>> response = ApiManagers.get("auction/lowest-sell-price/size", params, ref);

        if (ApiManagers.isSuccessful(response) && response.getBody() != null) {
            return response.getBody();
        }
        return Collections.emptyList();
    }

    public void addProductLike(int userId, int productId) {
        Map<String, String> params = Map.of(
                "user_id", String.valueOf(userId),
                "product_id", String.valueOf(productId)
        );

        ResponseEntity<Void> response = ApiManagers.post("product/like", params, new TypeReference<Void>() {});

        if (!ApiManagers.isSuccessful(response)) {
            throw new RuntimeException("관심상품 등록 실패");
        }
    }

    public List<ProductKatteRecommendBean> getKatteRecommendList(int offset, int size) {
        Map<String, String> params = Map.of(
                "offset", String.valueOf(offset),
                "size", String.valueOf(size)
        );

        TypeReference<List<ProductKatteRecommendBean>> ref = new TypeReference<>() {};
        ResponseEntity<List<ProductKatteRecommendBean>> response = ApiManagers.get("product/recommend/katte_top", params, ref);

        // ✅ 여기 추가
        System.out.println("응답 내용: " + response);

        if (ApiManagers.isSuccessful(response) && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("캇테 추천상품 조회 실패");
        }

    }

    public List<ProductInfoBean> getBrandTopProducts(String brandName, int offset, int size) {
        Map<String, String> params = Map.of(
                "brand_name", brandName,
                "offset", String.valueOf(offset),
                "size", String.valueOf(size)
        );
        System.out.println("🔍 브랜드 탑 상품 API 호출 브랜드명: " + brandName);
        TypeReference<List<ProductInfoBean>> ref = new TypeReference<>() {};
        ResponseEntity<List<ProductInfoBean>> response = ApiManagers.get("product/brand/brand_top", params, ref);

        if (ApiManagers.isSuccessful(response)) {
            return response.getBody();
        } else {
            throw new RuntimeException("브랜드 Top 상품을 불러오는 데 실패했습니다.");
        }
    }

    public Integer getUserKatteMoney(int userId) {
        Map<String, String> params = Map.of("user_id", String.valueOf(userId));
        TypeReference<UserPaymentBean> ref = new TypeReference<>() {};
        ResponseEntity<UserPaymentBean> response = ApiManagers.get("user/payment", params, ref);

        if (ApiManagers.isSuccessful(response) && response.getBody() != null) {
            return response.getBody().getKatte_money();
        }
        return 0;
    }

    public String getAuctionEndTime(int productId, String size) {
        Map<String, String> params = Map.of(
                "product_id", String.valueOf(productId),
                "size", size
        );
        TypeReference<String> ref = new TypeReference<>() {};
        ResponseEntity<String> response = ApiManagers.get("auction/end_time", params, ref);

        if (ApiManagers.isSuccessful(response)) {
            return response.getBody(); // ← "2025-07-22 23:59"
        }
        return "마감 정보 없음";
    }

    // 공통 예외 처리
    private <T> T getOrThrow(ResponseEntity<T> response, String errorMsg) {
        if (ApiManagers.isSuccessful(response) && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException(errorMsg);
        }
    }

    public void insertOrder(EcommerceOrderBean orderBean) {
        ResponseEntity<String> response = ApiManagers.post("ecommerce/order", orderBean, new TypeReference<>() {});
        System.out.println(response);
        if (!ApiManagers.isSuccessful(response)) {
            System.out.println("주문 업로드를 실패했씁니다.");
        }
        else {
            System.out.println("주문 업로드를 성공했씁니다.");
        }
    }

    public AuctionDataBean getAuctionData(int product_id, int instant_price) {
        Map<String, String> params = new HashMap<>();
        params.put("product_id", String.valueOf(product_id));
        params.put("instant_price", String.valueOf(instant_price));
        TypeReference<AuctionDataBean> ref = new TypeReference<>() {};

        ResponseEntity<AuctionDataBean> response = ApiManagers.get("auction/getData", params, ref);
        return response.getBody();
    }
}
