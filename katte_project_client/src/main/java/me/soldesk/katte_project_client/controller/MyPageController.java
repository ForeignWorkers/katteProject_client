package me.soldesk.katte_project_client.controller;

import common.bean.ecommerce.EcommerceOrderHistoryBean;
import common.bean.product.ProductInfoBean;
import common.bean.user.UserAddressBean;
import common.bean.user.UserBean;
import jakarta.servlet.http.HttpSession;
import me.soldesk.katte_project_client.service.MyPageService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MyPageController {

    private final MyPageService myPageService;

    public MyPageController(MyPageService myPageService) { this.myPageService = myPageService;}


    /* ───────────────────────────────────────────────────────────────────────────
                                    구매 이력
   ─────────────────────────────────────────────────────────────────────────── */

    @GetMapping("/MyPage")
    public String getOrderListPage(HttpSession session , Model model) {
        UserBean user = (UserBean) session.getAttribute("currentUser");
        session.setAttribute("user_id", String.valueOf(user.getUser_id()));
        String user_id = (String)session.getAttribute("user_id");
        System.out.println("user_id : " + user_id);
        List<EcommerceOrderHistoryBean> orderList = myPageService.getOrderHistory(user_id);

        Map<Integer, ProductInfoBean> productInfoMap = new HashMap<>();
        if (orderList != null) {
            for (EcommerceOrderHistoryBean order : orderList) {
                int product_id = order.getProduct_id();
                ProductInfoBean productInfo = myPageService.getProductInfo(product_id);
                System.out.println(productInfo);
                if (productInfo != null) {
                    productInfoMap.put(product_id, productInfo);
                }
            }
        }

        model.addAttribute("orderList", orderList);
        model.addAttribute("productInfoMap", productInfoMap);

        System.out.println(orderList);
        System.out.println(productInfoMap);

        return "CsMyPage/MyPage";
    }

    @PostMapping("/MyPage/confirm")
    @ResponseBody
    public String confirmPurchase(@RequestParam("order_id") int order_id, HttpSession session) {
        UserBean user = (UserBean) session.getAttribute("currentUser");
        session.setAttribute("user_id", String.valueOf(user.getUser_id()));

        if (user == null) {
            return "로그인 해 주세요";
        }

        int user_id = user.getUser_id();
        return myPageService.confirmOrder(order_id, user_id);
    }


    /* ───────────────────────────────────────────────────────────────────────────
                                    관심
   ─────────────────────────────────────────────────────────────────────────── */


    @GetMapping("/Favorite")
    public String showFavoriteBrandPage(HttpSession session, Model model) {
        Object currentUserObj = session.getAttribute("currentUser");

        if (currentUserObj instanceof UserBean user) {
            String user_id = String.valueOf(user.getUser_id());

            model.addAttribute("favoriteBrands", myPageService.getUserFavoriteBrands(user_id));
            return "CsMyPage/Wishlist_brand";
        }

        return "redirect:/login";
    }

    /* ───────────────────────────────────────────────────────────────────────────
                                  내 스타일
   ─────────────────────────────────────────────────────────────────────────── */

    @GetMapping("/MyStyle")
    public String myStyle(){
        return "CsMyPage/Mypage_mystyle";
    }

    /* ───────────────────────────────────────────────────────────────────────────
                                  캇테 머니
   ─────────────────────────────────────────────────────────────────────────── */

    @GetMapping("/MyKatteMoney")
    public String myKatteMoney(){
        return "CsMyPage/Mypage_kattemoney_page";
    }

    /* ───────────────────────────────────────────────────────────────────────────
                                   내 포인트
   ─────────────────────────────────────────────────────────────────────────── */

    @GetMapping("/MyPoint")
    public String myPoint(){
        return "CsMyPage/Mypage_point";
    }

    /* ───────────────────────────────────────────────────────────────────────────
                                   내 쿠폰
   ─────────────────────────────────────────────────────────────────────────── */

    @GetMapping("/MyCoupon")
    public String myCoupon(){
        return "CsMyPage/Mypage_coupon";
    }


    /* ───────────────────────────────────────────────────────────────────────────
                                        주소록 관리
       ─────────────────────────────────────────────────────────────────────────── */

        //주소 조회 user_id 필요
        @GetMapping("/MyAddress")
        public String addressPage(HttpSession session, Model model) {
            UserBean user = (UserBean) session.getAttribute("currentUser");
            session.setAttribute("user_id", String.valueOf(user.getUser_id()));
            System.out.println(user.getUser_id());
            String user_id = (String) session.getAttribute("user_id");
            System.out.println("받은 유저 ID : " + user_id);

            if (user_id == null) {
                return "redirect:/login";
            }

            // 주소 목록
            ResponseEntity<List<UserAddressBean>> response = myPageService.getUserAddressList(user_id);
            System.out.println("받은 주소 목록 리스트 : " + response.getBody());
            model.addAttribute("addressList", response.getBody());

            // 메인 주소도 함께 가져오기
            ResponseEntity<UserAddressBean> mainResponse = myPageService.getUserMainAddress(user_id);
            System.out.println("메인 주소 리스트 : " + mainResponse.getBody());
            model.addAttribute("mainAddress", mainResponse.getBody());

            return "CsMyPage/MyPage_address";
        }

        //수정을 위한 단일 정보 취득
        @GetMapping("/MyAddress/edit")
        public String showEditForm(@RequestParam("address_id") String address_id,
                                   HttpSession session, Model model) {
            System.out.println("단일 정보 컨트롤러 탔어요");
            String user_id = (String) session.getAttribute("user_id");
            ResponseEntity<List<UserAddressBean>> response = myPageService.getUserAddressDetail(user_id, address_id);

            System.out.println("받은 값 : "+ response.getBody());

            // 응답이 정상이고 데이터가 존재할 경우
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && !response.getBody().isEmpty()) {
                UserAddressBean address = response.getBody().get(0); // 첫 번째 주소 정보 가져오기 ( 어차피 해당하는 애는 한 개 뿐임 )
                model.addAttribute("addressList", address);
            } else {
                System.out.println("주소 정보를 불러올 수 없습니다");
            }
            return "CsMyPage/MyPage_address_edit";
        }

        @PostMapping("/MyAddress/edit")
        public String editAddress(@ModelAttribute UserAddressBean bean, Model model) {
            System.out.println("주소 수정 컨트롤러 탔어요");
            System.out.println("받은 ID : " + bean.getId());
            System.out.println("받은 유저 ID : " + bean.getUser_id());
            System.out.println("수정된 이름 : " + bean.getName());
            System.out.println("수정된 전화번호 : " + bean.getPhone_number());
            System.out.println("수정된 주소 1" + bean.getAddress_line01());
            System.out.println("수정된 주소 1" + bean.getAddress_line02());
            System.out.println("수정된 우편번호 : " + bean.getPost_num());

            ResponseEntity<String> response = myPageService.editAddress(bean);
            model.addAttribute("addressDetail", response.getBody());
            return "CsMyPage/MyPage_address_ok";
        }

        // 작성 폼
        @GetMapping("/MyAddress/post")
        public String showAddressPost(HttpSession session) {
            System.out.println("주소 등록하기 컨트롤러 탔어요");
            System.out.println(session.getAttribute("user_id"));
            return "CsMyPage/MyPage_address_post";
        }


/*    @PostMapping("/MyAddress/add")
    @ResponseBody
    public String postAddress(@ModelAttribute UserAddressBean bean, HttpSession session) {
        int user_id = Integer.parseInt((String) session.getAttribute("user_id"));
        bean.setUser_id(user_id);
        myPageService.addAddress(bean);
        System.out.println("닫혀라");
        return "CsMyPage/MyPage_address";
    }*/
    @PostMapping("/MyAddress/add")
    public String postAddress(@ModelAttribute UserAddressBean bean, HttpSession session, Model model) {
        int user_id = Integer.parseInt((String) session.getAttribute("user_id"));
        bean.setUser_id(user_id);
        myPageService.addAddress(bean);
        System.out.println("닫혀라");

        return "CsMyPage/MyPage_address_ok";
    }

    // 메인 주소 설정
    @PostMapping("/MyAddress/set-main")
    public String setMainAddress(@RequestParam("address_id") String address_id, HttpSession session) {

        String user_id = (String) session.getAttribute("user_id");
        System.out.println("메인 주소 설정에 받은 유저ID : " + user_id);
        myPageService.setMainAddress(user_id, address_id);
        return "redirect:/MyAddress";
    }

    // 주소 삭제
    @PostMapping("/MyAddress/del")
    public String deleteAddress(@RequestParam("address_id") int address_id, HttpSession session) {
        String userId = (String) session.getAttribute("user_id");
        myPageService.deleteAddress(Integer.parseInt(userId), address_id);
        return "redirect:/MyAddress";
    }

}
