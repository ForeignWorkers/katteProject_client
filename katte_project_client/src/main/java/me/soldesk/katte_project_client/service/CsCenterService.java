package me.soldesk.katte_project_client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import common.bean.cs.CsAnnounceBean;
import common.bean.cs.CsFaqBean;
import common.bean.cs.CsInquireCustomerBean;
import common.bean.cs.CsStandardBean;
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
        TypeReference<List<CsAnnounceBean>> ref = new TypeReference<>() {};

        ResponseEntity<List<CsAnnounceBean>> response = ApiManagers.get("cs", params, ref);

        if (ApiManagers.isSuccessful(response) && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("공지사항 목록을 불러오는 데 실패했습니다.");
        }

    }

    //공지 수정에 쓰일 id 받아서 단일 공지 띄우는 친구
    public CsAnnounceBean getCsAnnounceBean(int announce_id) {
        System.out.println("단일 정보 서비스 탔어요");
        Map<String, String> params = new HashMap<>();
        params.put("announce_id", String.valueOf(announce_id));
        TypeReference<List<CsAnnounceBean>> ref = new TypeReference<>() {};

        ResponseEntity<List<CsAnnounceBean>> response = ApiManagers.get("cs", params, ref);

        if (ApiManagers.isSuccessful(response) && response.getBody() != null && response.getBody().size() > 0) {
            return response.getBody().get(0);
        } else {
            throw new RuntimeException("해당 공지사항을 찾을 수 없습니다.");
        }
    }

    //공지 작성
    public String postAnnounce(CsAnnounceBean bean) {
        System.out.println("공지 작성 서비스 탔어요");
        TypeReference<String> ref = new TypeReference<>() {};

        ResponseEntity<String> response = ApiManagers.post("cs/post", bean, ref);
        if (ApiManagers.isSuccessful(response)) {
            return response.getBody();
        } else {
            throw new RuntimeException("공지 등록이 실패하였습니다. " + response.getStatusCode());
        }
    }

    // FAQ 수정
    public String updateAnnounce(CsAnnounceBean bean) {
        System.out.println("공지 수정 서비스 탔어요");

        if (bean.getAnnounce_id() == 0) {
            throw new IllegalArgumentException("faq_id가 존재하지 않습니다. 수정이 불가능합니다.");
        }

        TypeReference<String> ref = new TypeReference<>() {};

        ResponseEntity<String> response = ApiManagers.patchBody("cs/edit", bean, ref);

        if (ApiManagers.isSuccessful(response)) {
            return response.getBody();
        } else {
            throw new RuntimeException("faq 수정이 실패하였습니다. " + response.getStatusCode());
        }
    }

    //공지 삭제
    public void deleteAnnounce(int announce_id) {
        System.out.println("공지 삭제 서비스 탔어요");
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("announce_id", announce_id);
        TypeReference<String> ref = new TypeReference<>() {};

        ResponseEntity<String> response = ApiManagers.deleteBody("cs/del", requestBody, ref);

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
        TypeReference<List<CsFaqBean>> ref = new TypeReference<>() {};

        ResponseEntity<List<CsFaqBean>> response = ApiManagers.get("cs/faq", params, ref);

        if (ApiManagers.isSuccessful(response) && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("자주 묻는 질문 목록을 불러오는 데 실패했습니다.");
        }

    }

    //수정에 쓰일 id 받아서 단일 정보 띄우는 친구
    public CsFaqBean getCsFaqBean(int faq_id) {
        System.out.println("faq 단일 정보 서비스 탔어요");
        Map<String, String> params = new HashMap<>();
        params.put("faq_id", String.valueOf(faq_id));

        TypeReference<List<CsFaqBean>> ref = new TypeReference<>() {};

        ResponseEntity<List<CsFaqBean>> response = ApiManagers.get("cs/faq", params, ref);
        if (ApiManagers.isSuccessful(response) && response.getBody() != null && !response.getBody().isEmpty()) {
            return response.getBody().get(0);
        } else {
            throw new RuntimeException("해당 공지사항을 찾을 수 없습니다.");
        }
    }

    //faq 작성
    public String postFaq(CsFaqBean bean) {
        System.out.println("faq 작성 서비스 탔어요");
        TypeReference<String> ref = new TypeReference<>() {};

        ResponseEntity<String> response = ApiManagers.post("cs/faq/post", bean, ref);
        if (ApiManagers.isSuccessful(response)) {
            return response.getBody();
        } else {
            throw new RuntimeException("faq 등록이 실패하였습니다. " + response.getStatusCode());
        }
    }

    // FAQ 수정
    public String updateFaq(CsFaqBean bean) {
        System.out.println("faq 수정 서비스 탔어요");

        if (bean.getFaq_id() == 0) {
            throw new IllegalArgumentException("faq_id가 존재하지 않습니다. 수정이 불가능합니다.");
        }

        TypeReference<String> ref = new TypeReference<>() {};

        ResponseEntity<String> response = ApiManagers.patchBody("cs/faq/edit", bean, ref);

        if (ApiManagers.isSuccessful(response)) {

            return response.getBody();
        } else {
            throw new RuntimeException("faq 수정이 실패하였습니다. " + response.getStatusCode());
        }
    }



    //faq 삭제
    public void deleteFaq(int faq_id) {
        System.out.println("faq 삭제 서비스 탔어요");
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("faq_id", faq_id);
        TypeReference<String> ref = new TypeReference<>() {};

        ResponseEntity<String> response = ApiManagers.deleteBody("cs/faq/del", requestBody, ref);

        if (!ApiManagers.isSuccessful(response)) {
            throw new RuntimeException("faq 삭제에 실패했습니다. 상태 코드: " + response.getStatusCode());
        }
    }

        /* ───────────────────────────────────────────────────────────────────────────
                                   1:1 문의
   ─────────────────────────────────────────────────────────────────────────── */


    public List<CsInquireCustomerBean> getCsInquireCustomerBean(int user_id, int count, int offset) {
        System.out.println("문의 리스트 가져오기 서비스 탔어요");
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", String.valueOf(user_id));
        params.put("count", String.valueOf(count));
        params.put("offset", String.valueOf(offset));
        TypeReference<List<CsInquireCustomerBean>> ref = new TypeReference<>() {};

        ResponseEntity<List<CsInquireCustomerBean>> response = ApiManagers.get("cs/inquire", params, ref);

        if (ApiManagers.isSuccessful(response) && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("공지사항 목록을 불러오는 데 실패했습니다.");
        }

    }

    //공지 수정에 쓰일 id 받아서 단일 공지 띄우는 친구
    public CsInquireCustomerBean getCsInquireCustomerBean(int user_id, int inquire_id) {
        System.out.println("단일 정보 서비스 탔어요");
        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("inquire_id", String.valueOf(inquire_id));
        TypeReference<List<CsInquireCustomerBean>> ref = new TypeReference<>() {};

        ResponseEntity<List<CsInquireCustomerBean>> response = ApiManagers.get("cs", params, ref);

        if (ApiManagers.isSuccessful(response) && response.getBody() != null && response.getBody().size() > 0) {
            return response.getBody().get(0);
        } else {
            throw new RuntimeException("해당 공지사항을 찾을 수 없습니다.");
        }
    }

    //공지 작성
    public String postInquire_Customer(CsInquireCustomerBean bean) {
        System.out.println("문의 작성 서비스 탔어요");
        TypeReference<String> ref = new TypeReference<>() {};

        ResponseEntity<String> response = ApiManagers.post("cs/inquiry/post", bean, ref);
        if (ApiManagers.isSuccessful(response)) {
            return response.getBody();
        } else {
            throw new RuntimeException("문의 등록이 실패하였습니다. " + response.getStatusCode());
        }
    }

   /* ───────────────────────────────────────────────────────────────────────────
                                  검수 기준
   ─────────────────────────────────────────────────────────────────────────── */


    public List<CsStandardBean> getStandardsByCategory(String standardCategory) {
        Map<String, String> params = new HashMap<>();
        params.put("standard_category", standardCategory);

        TypeReference<List<CsStandardBean>> ref = new TypeReference<>() {};
        ResponseEntity<List<CsStandardBean>> response = ApiManagers.get("cs/standard", params, ref);

        if (ApiManagers.isSuccessful(response) && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("해당하는 검수 기준을 불러오지 못했습니다.");
        }
    }

    public CsStandardBean getStandardById(int standard_id) {
        System.out.println("카테고리 별 검수 기준 불러오기 서비스 탔어요");

        Map<String, String> params = new HashMap<>();
        params.put("standard_id", String.valueOf(standard_id));

        System.out.println("standard_id : " + standard_id);

        TypeReference<List<CsStandardBean>> ref = new TypeReference<>() {};
        ResponseEntity<List<CsStandardBean>> response = ApiManagers.get("cs/standard/get", params, ref);

        if (ApiManagers.isSuccessful(response) && response.getBody() != null && !response.getBody().isEmpty()) {
            System.out.println("불러온 내용 : " + response.getBody());
            return response.getBody().get(0);
        } else {
            throw new RuntimeException("해당하는 카테고리의 검수 기준을 찾지 못했습니다.");
        }
    }

    public String postStandard(CsStandardBean bean) {
        System.out.println("Standard 등록 서비스 탔어요");

        TypeReference<String> ref = new TypeReference<>() {};
        ResponseEntity<String> response = ApiManagers.post("cs/standard/post", bean, ref);

        if (ApiManagers.isSuccessful(response)) {
            return response.getBody();
        } else {
            throw new RuntimeException("검수 기준의 등록에 실패했습니다. status_code : " + response.getStatusCode());
        }
    }

    // 수정하기
    public String editStandard(CsStandardBean bean) {
        System.out.println("검수 기준 수정 서비스 탔어요");

        if (bean.getStandard_id() == 0) {
            throw new IllegalArgumentException("standard_id가 지정되지 않아 수정할 수 없습니다.");
        }

        TypeReference<String> ref = new TypeReference<>() {};
        ResponseEntity<String> response = ApiManagers.patchBody("cs/standard/edit", bean, ref);

        if (ApiManagers.isSuccessful(response)) {
            return response.getBody();
        } else {
            throw new RuntimeException("검수 기준의 수정에 실패했습니다. status_code : " + response.getStatusCode());
        }
    }

    public void deleteStandard(int standard_id) {
        System.out.println("Standard 삭제 서비스 탔어요");

        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("standard_id", standard_id);

        TypeReference<String> ref = new TypeReference<>() {};
        ResponseEntity<String> response = ApiManagers.deleteBody("cs/standard/del", requestBody, ref);

        if (!ApiManagers.isSuccessful(response)) {
            throw new RuntimeException("검수 기준을 삭제하지 못했습니다. status_code: " + response.getStatusCode());
        }
    }
}