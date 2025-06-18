package me.soldesk.katte_project_client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import common.bean.ecommerce.EcommerceOrderHistoryBean;
import common.bean.product.ProductBrandLikeBean;
import common.bean.product.ProductInfoBean;
import common.bean.user.UserAddressBean;
import me.soldesk.katte_project_client.manager.ApiManagers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MyPageService {
    /* ───────────────────────────────────────────────────────────────────────────
                                            구매이력
           ─────────────────────────────────────────────────────────────────────────── */
    //주문 이력 리스트 뽑기
    public List<EcommerceOrderHistoryBean> getOrderHistory(String user_id) {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        System.out.println(user_id);
        ResponseEntity<List<EcommerceOrderHistoryBean>> response = ApiManagers.get(
                "ecommerce/order/history",
                params,
                new TypeReference<>() {
                }
        );
        System.out.println("리스트 뽑기 서비스 탔어요");
        System.out.println(response.getBody());

        if (ApiManagers.isSuccessful(response)) {
            return response.getBody();
        } else {
            return null;
        }
    }

    //product_id 받아서 상품 정보 반환
    public ProductInfoBean getProductInfo(int product_id) {
        Map<String, String> params = new HashMap<>();
        params.put("product_id", String.valueOf(product_id));
        System.out.println("product_id 받아서 물품 정보 받는 서비스 탔어요");
        System.out.println("params 값 : " + params);
        System.out.println("Calling URL: /ecommerce/order/history/detail?product_id=" + product_id);

        ResponseEntity<ProductInfoBean> response = ApiManagers.get(
                "ecommerce/order/history/detail",
                params,
                new TypeReference<>() {}
        );

        System.out.println("response 내용 확인 : " +response.getBody());

        if (ApiManagers.isSuccessful(response) && response.getBody() != null) {
            return response.getBody();
        }

        if (ApiManagers.isSuccessful(response)) {
            System.out.println("서비스 : " + response.getBody());
            return response.getBody();
        }
        System.out.println("response가 비어있음.");
        return null;
    }

    // 구매 확정 요청
    public String confirmOrder(int order_id, int user_id) {
        Map<String, String> params = new HashMap<>();
        params.put("order_id", String.valueOf(order_id));
        params.put("user_id", String.valueOf(user_id));

        ResponseEntity<String> response = ApiManagers.patchQuery(
                "ecommerce/order/confirm",
                params,
                new TypeReference<>() {}
        );

        if (ApiManagers.isSuccessful(response)) {
            return response.getBody();
        } else {
            return "구매 확정 실패: " + (response.getBody() != null ? response.getBody() : "서버 오류");
        }
    }



    /* ───────────────────────────────────────────────────────────────────────────
                                    관심
   ─────────────────────────────────────────────────────────────────────────── */
    //관심 브랜드 최신순 조회
    public ResponseEntity<List<ProductBrandLikeBean>> getAllBrandLike(String user_id){
        Map<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        return ApiManagers.get("user/favorite/brand", params, new TypeReference<>() {});
    }

    public List<ProductBrandLikeBean> getUserFavoriteBrands(String user_id) {
        TypeReference<List<ProductBrandLikeBean>> ref = new TypeReference<>() {};

        ResponseEntity<List<ProductBrandLikeBean>> response = ApiManagers.get(
                "user/favorite/brand",
                Map.of("user_id", user_id),
                ref
        );

        return response.getBody();
    }



    /* ───────────────────────────────────────────────────────────────────────────
                                    내 주소록
   ─────────────────────────────────────────────────────────────────────────── */
    // 주소 등록
    public static ResponseEntity<String> addAddress(UserAddressBean bean) {
        System.out.println("user_id: " + bean.getUser_id());
        System.out.println("id : " + bean.getId());
        System.out.println("name : " + bean.getName());
        System.out.println("phone : " + bean.getPhone_number());
        System.out.println("address1 : " + bean.getAddress_line01());
        System.out.println("address2 : " + bean.getAddress_line02());
        System.out.println("is_main : " + bean.getIs_main());
        System.out.println("post_num : " + bean.getPost_num());
        return ApiManagers.post("user/address", bean, new TypeReference<>() {});
    }

    // 유저가 등록한 모든 주소 조회
    public static ResponseEntity<List<UserAddressBean>> getUserAddressList(String user_id) {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        return ApiManagers.get("user/address", params, new TypeReference<>() {});
    }

    //수정에 쓰일 id 받아서 단일 정보 띄우는 친구
    public static ResponseEntity<List<UserAddressBean>> getUserAddressDetail(String user_id, String id) {
        System.out.println("내 주소 단일 정보 서비스 탔어요");
        Map<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("id", id);

        return ApiManagers.get("user/address/detail", params, new TypeReference<>() {});
    }

    // 유저의 메인 주소 조회
    public ResponseEntity<UserAddressBean> getUserMainAddress(String user_id) {
        TypeReference<UserAddressBean> ref = new TypeReference<>() {};
        return ApiManagers.get("user/address/main", Map.of("user_id", user_id), ref);
    }

    // 메인 주소 변경 (Patch)
    public static ResponseEntity<String> setMainAddress(String user_id, String address_id) {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("address_id", address_id);
        System.out.println("user_id param : " + user_id);
        System.out.println("address_id param : " + address_id);

        return ApiManagers.patchQuery("user/address/main", params, new TypeReference<>() {});
    }

    public ResponseEntity<String> editAddress(UserAddressBean bean) {
        return ApiManagers.patchBody("user/address/edit", bean, new TypeReference<>() {});
    }

    // 주소 삭제
    public static ResponseEntity<String> deleteAddress(int user_id, int address_id) {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("address_id", String.valueOf(address_id));
        return ApiManagers.deleteQuery("user/address/del", params, new TypeReference<>() {});
    }

}
