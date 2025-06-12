// 패키지명을 하나로 통일: 예시로 me.soldesk.katte_project_client.controller 사용
package me.soldesk.katte_project_client.controller;

import me.soldesk.katte_project_client.service.StyleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class StyleuploadController {

    private final StyleService styleService;
    public StyleuploadController(StyleService styleService) {
        this.styleService = styleService;
    }

    @GetMapping("/styleupload")
    public String styleUpload() {

        return "/Stylepage/Style_upload";
    }

    @PostMapping("/styleupload")
    public String uploadStyle(
            @RequestParam String style_title,
            @RequestParam("images") MultipartFile[] images,
            @RequestParam String caption,
            @RequestParam String productTag,
            @RequestParam String hashtags
    ) {
        styleService.save(style_title,images, caption, productTag, hashtags);
        return "/CsMypage/Mypage_mystyle";
    }
}
