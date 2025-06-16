package me.soldesk.katte_project_client.controller;

import common.bean.auction.AuctionDataBean;
import common.bean.content.ContentShortBean;
import common.bean.content.ContentShortformBean;
import common.bean.product.ProductPerSaleBean;
import common.bean.user.UserBean;
import me.soldesk.katte_project_client.service.AuctionService;
import me.soldesk.katte_project_client.service.ShortformService;
import me.soldesk.katte_project_client.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
public class ContentController {

    @Autowired
    private ShortformService shortformService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuctionService auctionService;

    @Value("${server.resource.url}")
    private String resourceUrl;

    @Value("${server.resource.profile.basic}")
    private String basicProfileUrl;

    @GetMapping("/shortform/random")
    public ResponseEntity<ContentShortformBean> getRandomShort() {
        ContentShortformBean shortform = shortformService.getRandomShort();
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
}
