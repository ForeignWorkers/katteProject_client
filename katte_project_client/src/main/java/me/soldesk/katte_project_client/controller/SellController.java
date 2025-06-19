package me.soldesk.katte_project_client.controller;

import jakarta.servlet.http.HttpServletRequest;
import me.soldesk.katte_project_client.service.ProductListService;
import common.bean.admin.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SellController {

    @Autowired
    private ProductListService productListService;

    //판매 페이지 판매 등록(디폴트)으로 이동
    @GetMapping("/sell")
    public String sell() {
        return "Sell_product_upload/Sell_product_upload";

    }

    //상품 등록 탭 누를 시 페이지 이동
    @GetMapping("/sell/registration")
    public String sellRegistration() {
        return "Sell_product_upload/Sell_product_upload";

    }

    //등록 상품 관리 탭 누를 시 페이지로 이동
    @GetMapping("/sell/manage")
    public String showProductLists(
            @RequestParam(defaultValue = "1") int page1,
            @RequestParam(defaultValue = "1") int page2,
            Model model
    ) {
        int size1 = 3;
        int size2 = 2;

        List<RegisteredProductViewBean> registered = productListService.getRegisteredItems(page1, size1);
        List<SoldoutProductViewBean> soldout = productListService.getSoldoutItems(page2, size2); //변경


        //상태값을 한글로 읽을 수 있게 변환한 Map 생성
        Map<Integer, String> readableStepMap = new HashMap<>();
        for (int i = 0; i < registered.size(); i++) {
            String readable = getReadableSaleStep(registered.get(i).getSale_step().toString());
            readableStepMap.put(i, readable);
        }

        int registeredCount = productListService.getRegisteredCount();
        int soldoutCount = productListService.getSoldoutCount();

        int totalRegisteredPages = Math.max(1, (int) Math.ceil((double) registeredCount / size1));
        int totalSoldoutPages = Math.max(1, (int) Math.ceil((double) soldoutCount / size2));

        //페이징 테스트용 강제 설정
        totalRegisteredPages = Math.max(totalRegisteredPages, 10);
        totalSoldoutPages = Math.max(totalSoldoutPages, 10);

        //페이징 그룹 시작점 계산
        int groupStart1 = ((page1 - 1) / 3) * 3 + 1;
        int groupStart2 = ((page2 - 1) / 3) * 3 + 1;

        Map<Integer, String> saleLinkTextMap = new HashMap<>();
        Map<Integer, String> saleLinkActionMap = new HashMap<>();

        for (int i = 0; i < registered.size(); i++) {
            String step = registered.get(i).getSale_step().toString();
            switch (step) {
                case "INSPECTION":
                    saleLinkTextMap.put(i, "-");
                    saleLinkActionMap.put(i, ""); // 링크 없음
                    break;
                case "INSPECTION_FAIL":
                    saleLinkTextMap.put(i, "자세히보기");
                    saleLinkActionMap.put(i, "ALERT"); // alert 띄우기
                    break;
                case "ON_SALE":
                    saleLinkTextMap.put(i, "보러가기");
                    saleLinkActionMap.put(i, "/product/" + registered.get(i).getProduct_id()); // 상세페이지로 이동(추후 수정 해야함) getPer_sale_id
                    break;
                case "EXPIRED":
                    saleLinkTextMap.put(i, "삭제");
                    saleLinkActionMap.put(i, "/product/delete/" + registered.get(i).getPer_sale_id()); // 삭제 처리 경로 getPer_sale_id
                    break;
                default:
                    saleLinkTextMap.put(i, "-");
                    saleLinkActionMap.put(i, "");
            }
        }

        model.addAttribute("registered", registered);
        model.addAttribute("soldout", soldout);
        model.addAttribute("registeredCount", registeredCount);
        model.addAttribute("soldoutCount", soldoutCount);
        model.addAttribute("totalRegisteredPages", totalRegisteredPages);
        model.addAttribute("totalSoldoutPages", totalSoldoutPages);
        model.addAttribute("currentPage1", page1);
        model.addAttribute("currentPage2", page2);
        model.addAttribute("groupStart1", groupStart1);
        model.addAttribute("groupStart2", groupStart2);
        model.addAttribute("readableSteps", readableStepMap);
        model.addAttribute("saleLinkText", saleLinkTextMap);
        model.addAttribute("saleLinkAction", saleLinkActionMap);


        return "Sell_product_management/Sell_product_management";
    }

    //상태값 한글 전환
    private String getReadableSaleStep(String step) {
        return switch (step) {
            case "INSPECTION" -> "검수 중";
            case "INSPECTION_FAIL" -> "검수 기준 미달";
            case "ON_SALE" -> "판매 중";
            case "EXPIRED" -> "기간 만료";
            case "SOLD_OUT" -> "판매 완료";
            default -> "알 수 없음";
        };
    }

    @PostMapping("/sell/settle")
    public String settleAuction(@RequestParam("auction_id") int auctionId, HttpServletRequest request) {
        String result = productListService.settleAuction(auctionId);
        request.getSession().setAttribute("settleResult", result);
        return "redirect:/sell/manage";
    }
}