package me.soldesk.katte_project_client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import common.bean.auction.AuctionDataBean;
import common.bean.ecommerce.EcommerceCoupon;
import common.bean.ecommerce.EcommerceCouponHistory;
import me.soldesk.katte_project_client.manager.ApiManagers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class CouponService {

    public List<EcommerceCouponHistory> getCurrentCouponHistory(int user_id) {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", Integer.toString(user_id));
        TypeReference<List<EcommerceCouponHistory>> ref = new TypeReference<>() {};

        ResponseEntity<List<EcommerceCouponHistory>> res = ApiManagers.get("coupon/user",params,ref);
        return res.getBody();
    }

    public EcommerceCoupon getCurrentCoupon(int coupon_id) {
        Map<String, String> params = new HashMap<>();
        params.put("coupon_id", Integer.toString(coupon_id));
        TypeReference<EcommerceCoupon> ref = new TypeReference<>() {};

        ResponseEntity<EcommerceCoupon> res = ApiManagers.get("coupon/data",params,ref);
        return res.getBody();
    }

    public String addCoupon(int coupon_id, int user_id) {
        LocalDateTime sixtyDaysLater = LocalDateTime.now().plusDays(60);
        Date endDate = Date.from(sixtyDaysLater.atZone(ZoneId.systemDefault()).toInstant());

        EcommerceCouponHistory coupon = new EcommerceCouponHistory();
        coupon.setCoupon_id(coupon_id);
        coupon.setUser_id(user_id);
        coupon.setEnd_date(endDate);
        coupon.setStart_date(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

        TypeReference<String> ref = new TypeReference<>() {};
        ResponseEntity<String> response = ApiManagers.post("ecommerce/coupon/assign", coupon, ref);

        System.out.println(response);
        System.out.println("회원 가입을 해서 쿠폰을 등록하였습니다." + coupon_id + user_id);
        System.out.println("response: " + response.getBody());

        return response.getBody();
    }
}
