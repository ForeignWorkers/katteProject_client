package me.soldesk.katte_project_client.controller;

import common.bean.content.ContentStyleBean;
import common.bean.content.ContentStyleComment;
import common.bean.product.ProductInfoBean;
import common.bean.user.UserBean;
import me.soldesk.katte_project_client.service.StyleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.context.annotation.SessionScope;

import java.io.IOException;
import java.util.List;

@Controller
public class StyleController {

    private final StyleService styleService;
    public StyleController(StyleService styleService) {
        this.styleService = styleService;
    }

    /**
     * 스타일 목록 페이지
     * @param page 0부터 시작하는 페이지 번호 (없으면 0)
     */
    @GetMapping("/style")
    public String style(
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        int size = 10;  // 한 페이지당 보여줄 갯수
        List<ContentStyleBean> styles;
        try {
            styles = styleService.getPage(page, size);
        } catch (IOException e) {
            // TODO: 실제 운영에선 로깅하고 에러 페이지로 리다이렉트하거나 적절한 처리를 합니다.
            e.printStackTrace();
            styles = List.of();
        }
        model.addAttribute("styles", styles);
        model.addAttribute("currentPage", page);
        return "Stylepage/Stylepage";
    }

    // 상세보기는 그대로
    @GetMapping("/style/detail")
    public String styleDetail() {
        return "Stylepage/Style_detail";
    }

    @GetMapping("/style/like")
    @ResponseBody
    public boolean styleToggle(@RequestParam("style_id") int style_id, @SessionAttribute("currentUser") UserBean user){
        return styleService.toggleLike(user.getUser_id(),style_id);
    }

    @GetMapping("/content/style")
    @ResponseBody
    public ContentStyleBean getStyleById(@RequestParam("style_id") int style_id){
        System.out.println("HELLO");
        return styleService.getStyleById(style_id);
    }

    @GetMapping("/content/isUserLike")
    @ResponseBody
    public List<Integer> getIsUserLike(@RequestParam("size") int size,
                                        @RequestParam("offset") int offset,
                                        @SessionAttribute("currentUser") UserBean user){
        return styleService.isLikeStyle(user.getUser_id(),size,offset);
    }

    @GetMapping("/content/isUserLikeAll")
    @ResponseBody
    public List<Integer> getIsUserLike(@SessionAttribute("currentUser") UserBean user){
        System.out.println("!!!!!!" + user.getUser_id());
        System.out.println("%$%$%$%$" + styleService.isLikeStyleAll(user.getUser_id()));

        return styleService.isLikeStyleAll(user.getUser_id());
    }

    @GetMapping("/content/stylePrdouct")
    @ResponseBody
    public List<Integer> getStylePrdouct(@RequestParam("style_id") int style_id){
        return styleService.getStyleProductId(style_id);
    }

    @GetMapping("/content/getProduct")
    @ResponseBody
    public ProductInfoBean getProduct(@RequestParam("product_id") int product_id){
        return styleService.getProductById(product_id);
    }

    @GetMapping("/comment/list")
    @ResponseBody
    public List<ContentStyleComment> getComments(@RequestParam("style_id") int style_id){
        return styleService.getCommentsByStyleId(style_id);
    }

    @GetMapping("/comment/add")
    @ResponseBody
    public boolean addComment(@RequestParam("style_id") int style_id,
                             @RequestParam("content") String content,
                             @SessionAttribute("currentUser") UserBean user) {
        return styleService.addComment(user.getUser_id(), style_id,content);
    }

    @GetMapping("/style/isLikeExist")
    @ResponseBody
    public boolean isExsit(@RequestParam("style_id") int style_id,
                           @SessionAttribute("currentUser") UserBean user) {

        return styleService.isExitLikeByUserId(style_id,user.getUser_id());
    }
}
