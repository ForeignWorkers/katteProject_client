package me.soldesk.katte_project_client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyPageController {

    @GetMapping("/MyPage")
    public String myPage(){
        return "redirect:/html/CsMyPage/MyPage.html";
    }

    @GetMapping("/Favorite")
    public String favorite(){
        return "redirect:/html/CsMyPage/Favorite.html";
    }

    @GetMapping("/MyStyle")
    public String myStyle(){
        return "redirect:/html/CsMyPage/Mypage_mystyle.html";
    }

    @GetMapping("/MyKatteMoney")
    public String myKatteMoney(){
        return "redirect:/html/CsMyPage/Mypage_kattemoney_page.html";
    }

    @GetMapping("/MyPoint")
    public String myPoint(){
        return "redirect:/html/CsMyPage/Mypage_point.html";
    }

    @GetMapping("/MyCoupon")
    public String myCoupon(){
        return "redirect:/html/CsMyPage/Mypage_coupon.html";
    }

    @GetMapping("/MyLogInfo")
    public String myLogInfo(){
        return "redirect:/html/CsMyPage/Mypage_login_info.html";
    }

    @GetMapping("/MyProfile")
    public String myProfile(){
        return "redirect:/html/CsMyPage/Mypage_profile.html";
    }

    @GetMapping("/MyAddress")
    public String myAddress(){
        return "redirect:/html/CsMyPage/Mypage_adress.html";
    }


}
