package me.soldesk.katte_project_client.service;

import com.fasterxml.jackson.core.type.TypeReference;

import common.bean.user.UserKatteMoneyLogBean;
import common.bean.user.UserKatteMoneyRefundBean;
import me.soldesk.katte_project_client.manager.ApiManagers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class EcommerceService {

    //충전
    public String chargeKatteMoney(UserKatteMoneyLogBean chargeRequest) {
        ResponseEntity<String> response = ApiManagers.patchBody(
                "user/katte",
                chargeRequest,
                new TypeReference<String>() {}
        );

        if (ApiManagers.isSuccessful(response)) {
            return response.getBody(); // "요청이 완료 되었습니다. 현재 머니 : 10000" 등의 메시지
        } else {
            return "충전 요청 실패";
        }
    }

    //환불
    public String requestKatteRefund(UserKatteMoneyRefundBean refundBean) {

        ResponseEntity<String> response = ApiManagers.post(
                "user/katte/refund",
                refundBean,
                new TypeReference<String>() {}
        );



        System.out.println("응답 상태 코드: " + response.getStatusCodeValue());
        System.out.println("응답 본문: " + response.getBody());


        System.out.println("=== 백엔드 요청 직전 상태 ===");
        System.out.println("user_id: " + refundBean.getUser_id());
        System.out.println("amount: " + refundBean.getAmount());
        System.out.println("account_number: " + refundBean.getAccount_number());
        System.out.println("bank_type: " + refundBean.getBank_type());

        if (ApiManagers.isSuccessful(response)) {
            return response.getBody(); // ex: "환불 요청서 생성 완료"
        } else {
            return "환불 요청 중 오류가 발생했습니다.";
        }
    }
}