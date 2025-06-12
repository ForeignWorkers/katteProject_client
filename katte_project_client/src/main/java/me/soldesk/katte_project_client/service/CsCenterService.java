package me.soldesk.katte_project_client.service;

import common.bean.cs.CsAnnounceBean;
import common.bean.cs.CsFaqBean;
import me.soldesk.katte_project_client.manager.ApiManagers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CsCenterService {

    /* ───────────────────────────────────────────────────────────────────────────
                                     공지사항
   ─────────────────────────────────────────────────────────────────────────── */

    public List<CsAnnounceBean> getCsAnnounceBean(int count, int offset) {
        System.out.println("공지 리스트 가져오기 서비스 탔어요");
        Map<String, String> params = new HashMap<String, String>();
        params.put("count", String.valueOf(count));
        params.put("offset", String.valueOf(offset));

        ResponseEntity<CsAnnounceBean[]> response = ApiManagers.get("cs", params, CsAnnounceBean[].class);

        if (ApiManagers.isSuccessful(response) && response.getBody() != null) {
            return Arrays.asList(response.getBody());
        } else {
            throw new RuntimeException("공지사항 목록을 불러오는 데 실패했습니다.");
        }

    }

    //공지 수정에 쓰일 id 받아서 단일 공지 띄우는 친구
    public CsAnnounceBean getCsAnnounceBean(int announce_id) {
        System.out.println("단일 정보 서비스 탔어요");
        Map<String, String> params = new HashMap<>();
        params.put("announce_id", String.valueOf(announce_id));

        ResponseEntity<CsAnnounceBean[]> response = ApiManagers.get("cs", params, CsAnnounceBean[].class);

        if (ApiManagers.isSuccessful(response) && response.getBody() != null && response.getBody().length > 0) {
            return response.getBody()[0];
        } else {
            throw new RuntimeException("해당 공지사항을 찾을 수 없습니다.");
        }
    }

    //공지 작성
    public String postAnnounce(CsAnnounceBean bean) {
        System.out.println("공지 작성 서비스 탔어요");
        ResponseEntity<String> response = ApiManagers.post("cs/post", bean, String.class);
        if (ApiManagers.isSuccessful(response)) {
            return response.getBody();
        } else {
            throw new RuntimeException("공지 등록이 실패하였습니다. " + response.getStatusCode());
        }
    }

    //공지 삭제
    public void deleteAnnounce(int announce_id) {
        System.out.println("공지 삭제 서비스 탔어요");
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("announce_id", announce_id);

        ResponseEntity<String> response = ApiManagers.deleteBody("cs/del", requestBody, String.class);

        if (!ApiManagers.isSuccessful(response)) {
            throw new RuntimeException("공지 삭제에 실패했습니다. 상태 코드: " + response.getStatusCode());
        }
    }



    /* ───────────────────────────────────────────────────────────────────────────
                                   자주 묻는 질문
   ─────────────────────────────────────────────────────────────────────────── */

    //자주 묻는 질문 리스트 가져오기
    public List<CsFaqBean> getCsFaqBean(int count, int offset) {
        System.out.println("faq 리스트 불러오기 서비스 탔어요");
        Map<String, String> params = new HashMap<String, String>();
        params.put("count", String.valueOf(count));
        params.put("offset", String.valueOf(offset));

        ResponseEntity<CsFaqBean[]> response = ApiManagers.get("cs/faq", params, CsFaqBean[].class);

        if (ApiManagers.isSuccessful(response) && response.getBody() != null) {
            return Arrays.asList(response.getBody());
        } else {
            throw new RuntimeException("자주 묻는 질문 목록을 불러오는 데 실패했습니다.");
        }

    }

    //수정에 쓰일 id 받아서 단일 정보 띄우는 친구
    public CsFaqBean getCsFaqBean(int faq_id) {
        System.out.println("faq 단일 정보 서비스 탔어요");
        Map<String, String> params = new HashMap<>();
        params.put("faq_id", String.valueOf(faq_id));

        ResponseEntity<CsFaqBean[]> response = ApiManagers.get("cs/faq", params, CsFaqBean[].class);

        if (ApiManagers.isSuccessful(response) && response.getBody() != null && response.getBody().length > 0) {
            return response.getBody()[0];
        } else {
            throw new RuntimeException("해당 공지사항을 찾을 수 없습니다.");
        }
    }

    //공지 작성
    public String postFaq(CsFaqBean bean) {
        System.out.println("faq 작성 서비스 탔어요");
        ResponseEntity<String> response = ApiManagers.post("cs/faq/post", bean, String.class);
        if (ApiManagers.isSuccessful(response)) {
            return response.getBody();
        } else {
            throw new RuntimeException("faq 등록이 실패하였습니다. " + response.getStatusCode());
        }
    }

    //공지 삭제
    public void deleteFaq(int faq_id) {
        System.out.println("faq 삭제 서비스 탔어요");
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("faq_id", faq_id);

        ResponseEntity<String> response = ApiManagers.deleteBody("cs/faq/del", requestBody, String.class);

        if (!ApiManagers.isSuccessful(response)) {
            throw new RuntimeException("faq 삭제에 실패했습니다. 상태 코드: " + response.getStatusCode());
        }
    }

}