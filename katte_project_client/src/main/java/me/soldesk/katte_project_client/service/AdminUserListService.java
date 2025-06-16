package me.soldesk.katte_project_client.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import me.soldesk.katte_project_client.manager.ApiManagers;
import common.bean.admin.UserAdminViewBean;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminUserListService {

    //전체 회원 리스트 조회
    public List<UserAdminViewBean> getUserList(int offset, int size, String filter) {
        Map<String, String> params = new HashMap<>();
        params.put("offset", String.valueOf(offset));
        params.put("size", String.valueOf(size));
        if (filter != null) {
            params.put("filter", filter);
        }

        ResponseEntity<List<UserAdminViewBean>> response = ApiManagers.get(
                "admin/users",
                params,
                new TypeReference<>() {}
        );

        return response.getBody(); //혹은 null 체크 필요시 처리
    }

    //전체 회원 수 조회
    public int getUserTotalCount() {
        ResponseEntity<Integer> response = ApiManagers.get(
                "admin/users/count",
                null,
                new TypeReference<>() {}
        );
        return response.getBody() != null ? response.getBody() : 0;
    }

    //회원 검색
    public List<UserAdminViewBean> searchUserList(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        String url = String.format("admin/users/search?keyword=%s&offset=%d&size=%d", keyword, offset, size);
        ResponseEntity<List<UserAdminViewBean>> response = ApiManagers.get(url, null, new TypeReference<>() {});
        return response.getBody();
    }

    //회원 검색 수 반환
    public int searchUserCount(String keyword) {
        String url = String.format("admin/users/search/count?keyword=%s", keyword);
        ResponseEntity<Integer> response = ApiManagers.get(url, null, new TypeReference<>() {});

        // 널 체크 안전 처리
        if (response.getBody() == null) {
            System.err.println("회원 수 조회 실패: 응답 값이 null입니다.");
            return 0; // 또는 에러 던지기
        }
        return response.getBody();
    }
}
