package me.soldesk.katte_project_client.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import common.bean.cs.CsAnnounceBean;
import common.bean.user.UserAddressBean;
import common.bean.user.UserBean;
import jakarta.servlet.http.HttpSession;
import me.soldesk.katte_project_client.manager.ApiManagers;
import me.soldesk.katte_project_client.service.MyPageService;
import me.soldesk.katte_project_client.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MyPageController {

    private final MyPageService myPageService;

    public MyPageController(MyPageService myPageService) { this.myPageService = myPageService;}

    @GetMapping("/MyPage")
    public String myPage(){
        return "CsMyPage/MyPage";
    }

    @GetMapping("/Favorite")
    public String favorite(){
        return "CsMyPage/Favorite";
    }

    @GetMapping("/MyStyle")
    public String myStyle(){
        return "CsMyPage/Mypage_mystyle";
    }

    @GetMapping("/MyKatteMoney")
    public String myKatteMoney(){
        return "CsMyPage/Mypage_kattemoney_page";
    }

    @GetMapping("/MyPoint")
    public String myPoint(){
        return "CsMyPage/Mypage_point";
    }

    @GetMapping("/MyCoupon")
    public String myCoupon(){
        return "CsMyPage/Mypage_coupon";
    }

 /*   @GetMapping("/MyAddress")
    public String myAddress(){
        return "CsMyPage/MyPage_address";
    }*/

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


    @PostMapping("/MyAddress/add")
    @ResponseBody
    public String postAddress(@ModelAttribute UserAddressBean bean, HttpSession session) {
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
