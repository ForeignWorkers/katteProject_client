package me.soldesk.katte_project_client.controller;

import common.bean.auction.AuctionDataBean;
import common.bean.content.ContentShortformBean;
import common.bean.ecommerce.EcommerceCoupon;
import common.bean.ecommerce.EcommerceCouponHistory;
import common.bean.ecommerce.EcommerceOrderBean;
import common.bean.product.*;
import common.bean.user.UserAddressBean;
import common.bean.user.UserBean;
import common.bean.user.UserKatteMoneyLogBean;
import common.bean.user.UserPaymentBean;
import jakarta.servlet.http.HttpSession;
import me.soldesk.katte_project_client.manager.ApiManagers;
import me.soldesk.katte_project_client.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.lang.model.type.ReferenceType;
import java.lang.ref.Reference;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class ProductController {

    @Autowired
    private ProductRegisterService productRegisterService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;

    @Autowired
    private MyPageService myPageService;
    @Autowired
    private EcommerceService ecommerceService;
    //입찰구매 등록

    //즉시구매 겟.
    @GetMapping("/product/instant_buy")
    public String showInstantBuyPage(@RequestParam("product_id") int productId,
                                     @RequestParam("size") String size,
                                     @RequestParam("price") int price,
                                     Model model) {

        model.addAttribute("product_id", productId);
        model.addAttribute("size", size);
        model.addAttribute("price", price);


        return "Product_instant_buybtn_Page";
    }

    //입찰 겟
    @GetMapping("/product/buy")
    public String showBuyPage(@RequestParam("product_id") int productId,
                              @RequestParam("size") String size,
                              @RequestParam("price") int price,
                              Model model, HttpSession session) {

        // 상품 정보 가져오기
        ProductInfoBean product = productService.getProductInfo(productId);
        // 💡 로그인된 user 정보 가져오기
        UserBean loginUser = (UserBean) session.getAttribute("currentUser");
        if (loginUser == null) {
            return "redirect:/login"; // 또는 에러 메시지 페이지
        }
        int userId = loginUser.getUser_id();

        // 💰 잔액 조회
        int katteMoney = productService.getUserKatteMoney(userId); // 이거 있어야 함
        model.addAttribute("katte_money", katteMoney);

        model.addAttribute("product", product);

        model.addAttribute("product_id", productId);
        model.addAttribute("size", size);
        model.addAttribute("price", price);

        // 최저 즉시 판매가
        Integer lowestsellprice = productService.getLowestSellPrice(productId) != null ? productService.getLowestSellPrice(productId) : 0;
        model.addAttribute("lowestsellprice", lowestsellprice);

        // ✅ 사이즈별 즉시 판매가 추가
        List<ProductSizeWithSellPriceBean> sellPriceList = productService.getSizePriceListByProductId(productId);
        model.addAttribute("sell_price_list", sellPriceList); // ✅ 추가

        // 2. 경매 정보 (마감일 포함)
        String auctionEndTime = productService.getAuctionEndTime(productId, size);
        System.out.println("🟡 클라이언트 auctionEndTime = " + auctionEndTime);
        model.addAttribute("auctionEndTime", auctionEndTime); // auction.auction_end_time 으로 접근 가능

        return "Productpage/Product_buybtn_Page";
    }

    @PostMapping("/product/payment")
    public String showPaymentPage(
            @RequestParam("product_id") int productId,
            @RequestParam("product_name") String productName,
            @RequestParam("brand_name") String brandName,
            @RequestParam("origin_price") int originPrice,
            @RequestParam("size") String size,
            @SessionAttribute("currentUser") UserBean userBean,
            Model model
    ) {
        ProductInfoBean product = new ProductInfoBean();
        product.setProduct_id(productId);
        product.setProduct_name(productName);
        product.setBrand_name(brandName);

        model.addAttribute("product", product);
        model.addAttribute("origin_price", originPrice);
        model.addAttribute("size", size);

        //User payment 가져오기
        UserPaymentBean paymentBean = userService.getUserPayment(userBean.getUser_id());
        model.addAttribute("userPoint", paymentBean.getPoint());
        model.addAttribute("katte_money", paymentBean.getKatte_money());

        //쿠폰 데이터 가져오기
        List<EcommerceCouponHistory> userCoupon = userService.getUserCouponHistory(userBean.getUser_id());
        List<EcommerceCoupon> couponDatas = new ArrayList<>();

        for (EcommerceCouponHistory coupon : userCoupon) {
            couponDatas.add(userService.getUserCoupon(coupon.getCoupon_id()));
        }
        model.addAttribute("userCoupons", couponDatas);

        // 유저 주소록 리스트 조회
        ResponseEntity<UserAddressBean> responseAddress = myPageService.getUserMainAddress(Integer.toString(userBean.getUser_id()));
        UserAddressBean userAddressBean = responseAddress.getBody();

        if(userAddressBean == null){
            userAddressBean = new UserAddressBean();
            userAddressBean.setUser_id(userBean.getUser_id());
            userAddressBean.setName("강호동");
            userAddressBean.setAddress_line01("서울시 행복구 행복동 12번지");
            userAddressBean.setPhone_number("010-2222-3333");
            userAddressBean.setIs_main(true);
            userAddressBean.setPost_num(1111);
        }

        // 모델에 담기
        model.addAttribute("addrList", userAddressBean);
        return "Productpage/Product_buybtn_next_Page";
    }

    //캇테머니 겟터
    @GetMapping("/product/kattePayment")
    public String showPaymentPage(@RequestParam("user_id") int userId, Model model) {
        int katteMoney = productService.getUserKatteMoney(userId);
        System.out.println("사용자 잔액: " + katteMoney);
        model.addAttribute("katte_money", katteMoney);
        return "Productpage/Product_buybtn_next_Page";

    }

    // 상품 상세 페이지 - 패스 변수로 product_id 받기
    @GetMapping("/product/{product_id}")
    public String showProductDetail(@PathVariable("product_id") int productId, Model model) {

        // 상품 정보
        ProductInfoBean product = productService.getProductInfo(productId);
        System.out.println("상품 브랜드명 확인: " + product.getBrand_name()); // [✔ 확인용 로그]
        System.out.println("👉 브랜드 탑 상품 API 호출 브랜드명: " + product.getBrand_name());
        model.addAttribute("product", product);

        System.out.println("넘어온 product_id: " + productId);

        // 가격 요약 정보
        ProductPriceSummaryBean priceSummary = productService.getProductPriceSummary(productId);
        model.addAttribute("price_summary", priceSummary);

        // 최저 즉시 판매가
        Integer lowestsellprice = productService.getLowestSellPrice(productId) != null ? productService.getLowestSellPrice(productId) : 0;
        model.addAttribute("lowestsellprice", lowestsellprice);

        // 사이즈별 즉시가 리스트
        List<ProductSizeWithPriceBean> sizePriceList = productService.getSizePriceList(productId);
        System.out.println("사이즈/가격 리스트 수: " + sizePriceList.size());
        for (ProductSizeWithPriceBean item : sizePriceList) {
            System.out.println("👉 사이즈: " + item.getAuction_size_value() + " / 즉시가: " + item.getPrice());
        }
        System.out.println(sizePriceList.size());
        System.out.println(sizePriceList);
        model.addAttribute("size_price_list", sizePriceList);
        // 사이즈별 즉시 판매가 용
        model.addAttribute("sell_price_list", productService.getSizePriceListByProductId(product.getProduct_id()));
        // 시세 데이터
        model.addAttribute("priceHistory1M", productService.getPriceHistory(productId, "1M"));
        model.addAttribute("priceHistory3M", productService.getPriceHistory(productId, "3M"));
        model.addAttribute("priceHistoryAll", productService.getPriceHistory(productId, "ALL"));

        //캇테추천(5)
        List<ProductKatteRecommendBean> katteRecommendList = productService.getKatteRecommendList(0, 5);
        model.addAttribute("katteRecommendList", katteRecommendList);

        // 브랜드 상품(5)
        List<ProductInfoBean> brandTopProducts = productService.getBrandTopProducts(product.getBrand_name(), 0, 5);
        model.addAttribute("brandTopList", brandTopProducts);

        String imageUrl = String.format("https://resources-katte.jp.ngrok.io/images/%d/%d_1.png", product.getProduct_id(), product.getProduct_id());
        model.addAttribute("resourceURL", imageUrl);

        return "Productpage/Productpage";
    }


    // 사이즈별 최저 즉시 판매가 JSON 응답
    @GetMapping("/api/product/sell-price-by-size")
    @ResponseBody
    public List<ProductSizeWithSellPriceBean> getLowestSellPriceBySize(@RequestParam("product_id") int productId) {
        return productService.getLowestSellPriceBySize(productId);
    }

    @GetMapping("/product/price-chart")
    public String showPriceChartPage(@RequestParam int product_id, @RequestParam(defaultValue = "1M") String period, Model model) {
        List<Map<String, Object>> priceHistory = productService.getPriceHistory(product_id, period);
        model.addAttribute("priceHistory", priceHistory);
        model.addAttribute("productId", product_id);
        model.addAttribute("period", period);
        return "Productpage/Productpage";
    }

    @PostMapping("/product/like")
    public String addToWishlist(@RequestParam("product_id") int productId, HttpSession session) {
        UserBean user = (UserBean) session.getAttribute("user");
        int userId = user.getUser_id();

        productService.addProductLike(userId, productId);  // 위 서비스 호출
        System.out.println("현재 세션 유저 ID: " + session.getAttribute("user_id"));
        return "redirect:/product/detail?product_id=" + productId;
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


        //Bean 세팅
        ContentShortformBean shortformBean = new ContentShortformBean();
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
        ProductRegisterResult result = productRegisterService.registerProductFlow(
                shortformBean, sizeBean, auctionBean, perSaleBean, checkResultBean
        );

        if (result.getPerSaleId() == -1) {
            model.addAttribute("message", result.getMessage());
            return "Sell_product_upload/Sell_product_upload";
        }

        //파일 업로드
        //파일 업로드 처리
        try {
            if (!file.getContentType().startsWith("video/")) {
                model.addAttribute("message", "동영상 파일만 업로드 가능");
                return "Sell_product_upload/Sell_product_upload";
            }

            String postId = String.valueOf(result.getPerSaleId());

            Map<String, Object> parts = new HashMap<>();
            parts.put("post_id", postId);
            parts.put("isStyle", false);
            parts.put("file", file);

            ResponseEntity<String> response = ApiManagers.postFile("media/upload", parts);
            if (!ApiManagers.isSuccessful(response)) {
                model.addAttribute("message", "업로드 실패");
                return "Sell_product_upload/Sell_product_upload";
            }

            // 동영상 경로 구성
            String videoUrl = "/media/mp4/short_" + postId + ".mp4";
            shortformBean.setContent_url(videoUrl); // 필요 시 이후 로직에서 사용

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "동영상 업로드 중 오류 발생");
            return "Sell_product_upload/Sell_product_upload";
        }

        model.addAttribute("message", result.getMessage());
        System.out.println("submitProduct() 실행됨");
        return "redirect:/sell/manage";
    }

    @PostMapping("/product/submitOrder")
    @ResponseBody
    public String submitOrder(@RequestBody EcommerceOrderBean orderDTO,
                              @RequestParam("point") int point,
                              @SessionAttribute("currentUser") UserBean user) {

        AuctionDataBean auctionBean = productService.getAuctionData(orderDTO.getProduct_id(),orderDTO.getOrigin_price());
        EcommerceOrderBean order = new EcommerceOrderBean();
        order.setUser_id(user.getUser_id());
        order.setProduct_id(orderDTO.getProduct_id());
        order.setOrigin_price(orderDTO.getOrigin_price());
        order.setFinal_price(orderDTO.getFinal_price());
        order.setUsed_coupon_id(orderDTO.getUsed_coupon_id());
        order.setAddress_key(orderDTO.getAddress_key());
        order.setOrder_status(orderDTO.getOrder_status());
        order.setOrdered_at(new Date());
        order.setOrder_confirm(order.isOrder_confirm());
        order.setAuction_id(auctionBean != null ? auctionBean.getId() : 7781);

        // terms_id, auction_id, per_sale_id는 상황에 맞게 지정
        order.setTerms_id(1); // 예: 기본 약관 id

        productService.insertOrder(order); // DB 삽입

        //금액 정산
        UserPaymentBean bean = userService.getUserPayment(user.getUser_id());
        int final_point = bean.getPoint() - point;

        //포인트 사용
        userService.setPoint(user.getUser_id(),final_point);

        //캇테 사용
        UserKatteMoneyLogBean katteMoneyLogBean = new UserKatteMoneyLogBean();
        katteMoneyLogBean.setUser_id(user.getUser_id());
        katteMoneyLogBean.setChange_amount(orderDTO.getFinal_price());
        katteMoneyLogBean.setReason(UserKatteMoneyLogBean.reason.USED);
        katteMoneyLogBean.setCreated_at(new Date());
        userService.useKatteMoney(katteMoneyLogBean);

        return "success";
    }
}