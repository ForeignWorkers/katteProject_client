package me.soldesk.katte_project_client.controller;

import common.bean.auction.AuctionDataBean;
import common.bean.content.ContentShortformBean;
import common.bean.product.ProductCheckResultBean;
import common.bean.product.ProductPerSaleBean;
import common.bean.product.ProductSizeBean;
import common.bean.user.UserBean;
import jakarta.servlet.http.HttpSession;
import me.soldesk.katte_project_client.service.ProductRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
public class ProductController {

    @Autowired
    private ProductRegisterService productRegisterService;

    //특정 상품 디테일 페이지 이동
    @GetMapping("/product/detail")
    public String productDetail() {
        return "Productpage/Productpage";
    }

    @PostMapping("/submit-product")
    public String submitProduct(

            @RequestParam("file") MultipartFile file,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String product_id,
            @RequestParam(required = false) String size_value,
            @RequestParam int start_price,
            @RequestParam(required = false) Integer instant_price,
            @RequestParam(required = false, defaultValue = "false") boolean is_instant_sale,
            @RequestParam String sale_period,
            @RequestParam String product_category,
            HttpSession session,
            Model model
    ) {
        System.out.println("submitProduct() 호출됨");

        //로그인 확인
        UserBean currentUser = (UserBean) session.getAttribute("currentUser");
        if (currentUser == null) {
            model.addAttribute("message", "로그인이 필요합니다.");
            return "Loginpage/Login";
        }

        //경매 시작가 최소값 체크 (여기 추가!)
        if (start_price < 1) {
            model.addAttribute("message", "시작가는 최소 1 이상이어야 합니다.");
            return "Sell_product_upload/Sell_product_upload";
        }

        //동영상 업로드 → URL 획득
        String videoUrl;
        try {
            if (!file.getContentType().startsWith("video/")) {
                model.addAttribute("message", "동영상 파일만 업로드 가능합니다.");
                return "Sell_product_upload/Sell_product_upload";
            }

            String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/uploads/videos/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(uploadDir + fileName);
            file.transferTo(dest);
            videoUrl = "/uploads/videos/" + fileName;
        } catch (IOException e) {
            model.addAttribute("message", "동영상 업로드 실패");
            return "Sell_product_upload/Sell_product_upload";
        }

        //Bean 세팅
        ContentShortformBean shortformBean = new ContentShortformBean();
        shortformBean.setContent_url(videoUrl);
        shortformBean.setTitle(title);
        shortformBean.setDescription(description);
        shortformBean.setProduct_id(Integer.parseInt(product_id));
        shortformBean.setAuthor_id(currentUser.getUser_id());

        ProductSizeBean sizeBean = null;
        if (size_value != null && !size_value.isBlank()) {
            sizeBean = new ProductSizeBean();
            sizeBean.setProduct_id(Integer.parseInt(product_id));
            sizeBean.setSize_value(size_value);
        }

        AuctionDataBean auctionBean = new AuctionDataBean();
        auctionBean.setProduct_id(Integer.parseInt(product_id));
        auctionBean.setStart_price(start_price);
        auctionBean.setSale_period(sale_period);
        auctionBean.setIs_instant_sale(is_instant_sale);
        if (instant_price != null) {
            auctionBean.setInstant_price(instant_price);
        }

        ProductPerSaleBean perSaleBean = new ProductPerSaleBean();
        perSaleBean.setSale_user_id(currentUser.getUser_id());
        perSaleBean.setProduct_id(Integer.parseInt(product_id));
        // 숏폼 ID, 경매 ID는 내부 서비스에서 등록 후 최신 ID를 조회하여 주입

        //검수 요청용 Bean 생성
        ProductCheckResultBean checkResultBean = new ProductCheckResultBean();
        checkResultBean.setRequest_user_id(currentUser.getUser_id()); //현재 로그인 유저
        checkResultBean.setCheck_user_id(1); // 예시: 관리자 ID (나중에 교체 가능)
        checkResultBean.setCheck_desc("검수 요청합니다");

        //서비스 실행
        String result = productRegisterService.registerProductFlow(shortformBean, sizeBean, auctionBean, perSaleBean, checkResultBean);
        model.addAttribute("message", result);
        System.out.println("submitProduct() 실행됨");
        System.out.println("result: " + result);
        return "redirect:/sell/manage";
    }
}
