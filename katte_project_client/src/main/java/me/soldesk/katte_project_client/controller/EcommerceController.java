package me.soldesk.katte_project_client.controller;

import common.bean.user.UserBean;
import common.bean.user.UserKatteMoneyLogBean;
import common.bean.user.UserKatteMoneyRefundBean;
import jakarta.servlet.http.HttpSession;
import me.soldesk.katte_project_client.service.EcommerceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class EcommerceController {

    @Autowired
    private EcommerceService ecommerceService;

    @GetMapping("/mypage/kattemoney")
    public String moveToKatteMoneyPage(HttpSession session, Model model) {
        UserBean currentUser = (UserBean) session.getAttribute("currentUser");
        if (currentUser != null) {
            model.addAttribute("userName", currentUser.getFirst_name() + currentUser.getSecond_name());
        }
        model.addAttribute("tab", "charge"); //충전 탭 활성화를 위한 필수값
        return "CsMyPage/Mypage_kattemoney_charge";
    }


    @PostMapping("/charge")
    public String chargeKatte(@ModelAttribute UserKatteMoneyLogBean logBean, Model model) {
        String result = ecommerceService.chargeKatteMoney(logBean);
        model.addAttribute("message", result);
        return "CsMyPage/Mypage_kattemoney_charge"; // 충전 결과 보여줄 페이지
    }

    //실제 충전 처리 로직
    @GetMapping("/ecommerce/charge/confirm")
    public String handleChargeSuccess(@ModelAttribute("chargedAmount") Integer amount,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        if (amount == null) return "redirect:/mypage/kattemoney";

        UserBean currentUser = (UserBean) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        UserKatteMoneyLogBean logBean = new UserKatteMoneyLogBean();
        logBean.setUser_id(currentUser.getUser_id());
        logBean.setChange_amount(amount);
        logBean.setReason(UserKatteMoneyLogBean.reason.CHARGE);

        String result = ecommerceService.chargeKatteMoney(logBean);

        // 우회 시 백앤드에서 2중으로 막음
        if (amount < 10000 || amount > 1000000 || amount % 10000 != 0) {
            redirectAttributes.addFlashAttribute("message", "⚠️ 충전 금액은 10,000원 이상, 1,000,000원 이하이며 10,000원 단위여야 합니다.");
            return "redirect:/mypage/kattemoney";
        }

        //최종 알림 메시지를 Flash로 전달
        redirectAttributes.addFlashAttribute("message",String.format("%,dKatte가 충전되었습니다.", amount));
        return "redirect:/mypage/kattemoney";  // ✅ 최종 목적지
    }

    //차지 성공
    @GetMapping("/ecommerce/charge/success")
    public String handleChargeRedirect(@RequestParam int amount, HttpSession session, RedirectAttributes redirectAttributes) {
        // 새로고침 방지용: POST로 넘길 데이터만 flash로 전달
        redirectAttributes.addFlashAttribute("chargedAmount", amount);
        return "redirect:/ecommerce/charge/confirm";
    }

    //충전 실패
    @GetMapping("/ecommerce/charge/fail")
    public String handleChargeFail(Model model) {
        model.addAttribute("message", "결제가 실패했습니다. 다시 시도해주세요.");
        return "CsMyPage/Mypage_kattemoney_charge";
    }

    //환불 등록
    @PostMapping("/ecommerce/refund")
    public String handleKatteRefund(@ModelAttribute UserKatteMoneyRefundBean refundBean,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {

        UserBean currentUser = (UserBean) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // 디버깅용 출력
        System.out.println("===== [환불 요청 도착] =====");
        System.out.println("user_id: " + currentUser.getUser_id());
        System.out.println("amount: " + refundBean.getAmount());
        System.out.println("account_number: " + refundBean.getAccount_number());
        System.out.println("bank_type: " + refundBean.getBank_type());

        refundBean.setUser_id(currentUser.getUser_id());
        refundBean.setStatus(UserKatteMoneyRefundBean.status.REQUESTED);

        String result = ecommerceService.requestKatteRefund(refundBean);
        redirectAttributes.addFlashAttribute("message", result);

        return "redirect:/mypage/kattemoney/refund";
    }



    @GetMapping("/mypage/kattemoney/refund")
    public String moveToKatteMoneyRefundPage(HttpSession session, Model model) {
        UserBean currentUser = (UserBean) session.getAttribute("currentUser");
        if (currentUser != null) {
            model.addAttribute("userName", currentUser.getFirst_name() + currentUser.getSecond_name());
        }
        model.addAttribute("tab", "refund");  // 탭 상태 넘기기
        return "CsMyPage/Mypage_kattemoney_refund";
    }

}
