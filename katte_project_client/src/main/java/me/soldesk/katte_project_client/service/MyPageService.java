package me.soldesk.katte_project_client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import common.bean.cs.CsFaqBean;
import common.bean.user.UserAddressBean;
import me.soldesk.katte_project_client.manager.ApiManagers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MyPageService {

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

/*
    public static ResponseEntity<UserAddressBean> getUserMainAddress(String user_id) {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        return ApiManagers.get("user/address/main", params, new TypeReference<>() {});
    }*/
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
