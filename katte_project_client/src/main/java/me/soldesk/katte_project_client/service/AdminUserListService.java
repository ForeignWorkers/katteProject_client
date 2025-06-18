package me.soldesk.katte_project_client.service;

import common.bean.user.UserBanBean;
import common.bean.user.UserRestrictionBean;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import me.soldesk.katte_project_client.manager.ApiManagers;
import common.bean.admin.UserAdminViewBean;
import org.springframework.http.ResponseEntity;

import java.util.Date;
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

    // AdminUserListService.java
    public int getUserTotalCount(String filter) {
        Map<String, String> params = new HashMap<>();
        if (filter != null) params.put("filter", filter);

        ResponseEntity<Integer> response = ApiManagers.get(
                "admin/users/count",
                params,
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

    //---------------------------------------- 회원 상태 처리 ---------------------------------------------------------//

    public void changeUserStatus(int userId, String actionType, Integer stopDays) {
        switch (actionType) {
            // 정지 등록
            case "ban" -> {
                UserBanBean bean = new UserBanBean();
                bean.setUser_id(userId);
                ApiManagers.post("admin/users/ban", bean, new TypeReference<String>() {});
            }
            // 정지 해제
            case "unban" -> {
                Map<String, String> params = Map.of("user_id", String.valueOf(userId));
                ApiManagers.deleteQuery("admin/users/ban", params, new TypeReference<String>() {});
            }
            // 스타일 or 댓글 제한 등록
            case "style", "comment" -> {
                if (stopDays == null) {
                    throw new IllegalArgumentException("stop_days 값이 필요합니다.");
                }

                UserRestrictionBean bean = new UserRestrictionBean();
                bean.setUser_id(userId);
                bean.setRestriction_type(actionType);
                bean.setStop_days(stopDays);

                ApiManagers.post("admin/users/restriction", bean, new TypeReference<String>() {});
            }
            // 제한 해제
            case "normal" -> {
                // stopDays를 통해 어떤 제한만 해제할지 전달받는 구조로 변경
                // stopDays가 1이면 style, 2이면 comment, null이면 전체 해제
                Map<String, String> params;

                if (stopDays != null) {
                    String type = (stopDays == 1) ? "style" : "comment";
                    params = Map.of(
                            "user_id", String.valueOf(userId),
                            "restriction_type", type
                    );
                } else {
                    // 모든 제한 해제
                    params = Map.of("user_id", String.valueOf(userId));
                }

                ApiManagers.deleteQuery("admin/users/restriction", params, new TypeReference<String>() {});
            }
        }
    }

    //현재 유저 제한 목록 조회
    public List<UserRestrictionBean> getUserRestrictions(int userId) {
        Map<String, String> params = Map.of("user_id", String.valueOf(userId));

        System.out.println("요청 URI: admin/users/restrictions?user_id=" + userId);
        System.out.println("params: " + params);

        ResponseEntity<List<UserRestrictionBean>> response = ApiManagers.get(
                "admin/users/restrictions",
                params,
                new TypeReference<>() {}
        );

        System.out.println("🧪 응답 본문: " + response.getBody());
        return response.getBody() != null ? response.getBody() : List.of();
    }

    //특정 제한 여부 (style/comment) 여부 확인
    public boolean isUserRestricted(int userId, String restrictionType) {
        Map<String, String> params = Map.of(
                "user_id", String.valueOf(userId),
                "restriction_type", restrictionType
        );

        ResponseEntity<Boolean> response = ApiManagers.get(
                "admin/users/restriction/check",
                params,
                new TypeReference<>() {}
        );

        return Boolean.TRUE.equals(response.getBody());
    }



}
