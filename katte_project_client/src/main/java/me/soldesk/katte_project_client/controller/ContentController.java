package me.soldesk.katte_project_client.controller;

import common.bean.auction.AuctionDataBean;
import common.bean.content.*;
import common.bean.ecommerce.EcommerceOrderBean;
import common.bean.ecommerce.EcommerceTradeLookUp;
import common.bean.product.ProductPerSaleBean;
import common.bean.user.UserBean;
import me.soldesk.katte_project_client.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ContentController {

    @Autowired
    private ShortformService shortformService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private StyleService styleService;

    @Value("${server.resource.url}")
    private String resourceUrl;

    @Value("${server.resource.profile.basic}")
    private String basicProfileUrl;

    @GetMapping("/shortform/random")
    public ResponseEntity<ContentShortformBean> getRandomShort(@RequestParam("selectId") Integer selectId) {

        ContentShortformBean shortform = new ContentShortformBean();
        if(selectId != null) {
            shortform = shortformService.getShortData(selectId);
        }
        else {
            shortform = shortformService.getRandomShort();
        }

        UserBean bean = userService.getUserByUserId(shortform.getAuthor_id());

        shortform.setProfileImgUrl(bean.getProfile_url() == null ? resourceUrl+basicProfileUrl : bean.getProfile_url());

        ProductPerSaleBean perSaleBean = shortformService.getProductPerSaleUseShortId(shortform.getId());

        if (perSaleBean != null) {
            AuctionDataBean auctionDataBean = auctionService.getAuctionData(perSaleBean.getAuction_data_id());

            shortform.setCurrentPrice(Integer.toString(auctionDataBean.getCurrent_price()));
            shortform.setInstantPrice(Integer.toString(auctionDataBean.getInstant_price()));
        }

        return ResponseEntity.ok(shortform);
    }

    @GetMapping("/shortform/like")
    @ResponseBody
    public boolean toggleLike(@RequestParam("short_id") int shortId, @SessionAttribute("currentUser") UserBean user) {
        return shortformService.toggleLike(user.getUser_id(), shortId);
    }

    @GetMapping("/price_history")
    @ResponseBody
    public List<EcommerceTradeLookUp> getPriceHistory(
            @RequestParam String range,
            @RequestParam int productId
    ) {
        return shortformService.getTrades(
                String.valueOf(productId),
                range
        );
    }

    @GetMapping("/content/styleProductId")
    @ResponseBody
    public List<ContentStyleBean> getStyleProducts(@RequestParam String product_id) {
        System.out.println(product_id);
        List<ContentStyleBean> listBena = new ArrayList<>();
        List<ContentStyleProductJoinBean> result = contentService.getContentStyleProducts(product_id);
        for (ContentStyleProductJoinBean contentStyleProductJoinBean : result) {
            System.out.println(contentStyleProductJoinBean);
            listBena.add(contentService.getContentStyle(contentStyleProductJoinBean.getStyle_id()));
        }
        return listBena;
    }
}
