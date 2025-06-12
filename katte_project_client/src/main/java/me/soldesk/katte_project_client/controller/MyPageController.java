package me.soldesk.katte_project_client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyPageController {

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

    @GetMapping("/MyLogInfo")
    public String myLogInfo(){
        return "CsMyPage/Mypage_login_info";
    }

    @GetMapping("/MyProfile")
    public String myProfile(){
        return "CsMyPage/Mypage_profile";
    }

    @GetMapping("/MyAddress")
    public String myAddress(){
        return "html/CsMyPage/Mypage_adress";
    }


}
