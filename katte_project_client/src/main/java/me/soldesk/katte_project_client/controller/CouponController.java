package me.soldesk.katte_project_client.controller;

import common.bean.ecommerce.EcommerceCoupon;
import common.bean.ecommerce.EcommerceCouponHistory;
import common.bean.user.UserBean;
import me.soldesk.katte_project_client.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CouponController {
    @Autowired
    private CouponService couponService;

    @GetMapping("/coupon/user")
    @ResponseBody
    public List<EcommerceCoupon> userCoupon(@SessionAttribute("currentUser") UserBean user) {
        List<EcommerceCoupon> coupons = new ArrayList<>();
        List<EcommerceCouponHistory> couponHistoryList = couponService.getCurrentCouponHistory(user.getUser_id());
        for (int i = 0; i < couponHistoryList.size(); i++) {
            EcommerceCoupon coupon = couponService.getCurrentCoupon(couponHistoryList.get(i).getCoupon_id());
            coupon.setEnd_date(couponHistoryList.get(i).getEnd_date());
            coupons.add(coupon);
        }
        return coupons;
    }
}
