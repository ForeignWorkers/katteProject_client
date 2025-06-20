package me.soldesk.katte_project_client.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.bean.user.UserBean;
import common.bean.user.UserRestrictionBean;
import jakarta.servlet.http.HttpSession;
import me.soldesk.katte_project_client.service.AdminUserListService;
import me.soldesk.katte_project_client.service.StyleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class StyleuploadController {

    private final StyleService styleService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private  AdminUserListService adminUserListService;

    public StyleuploadController(StyleService styleService) {
        this.styleService = styleService;
    }

    @GetMapping("/styleupload")
    public String styleUpload(HttpSession session) {
        UserBean currentUser = (UserBean) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // 제한 리스트 조회
        List<UserRestrictionBean> restrictions =
                adminUserListService.getUserRestrictions(currentUser.getUser_id());

        for (UserRestrictionBean restriction : restrictions) {
            // 스타일 제한이고, 제한 종료일이 오늘 이후라면 제한 중
            if ("style".equalsIgnoreCase(restriction.getRestriction_type())
                    && restriction.getEnd_date() != null
                    && restriction.getEnd_date().toInstant().isAfter(java.time.Instant.now())) {


                return "redirect:/MyStyle?restricted=true";
            }
        }

        return "Stylepage/Style_upload"; // 제한 없으면 업로드 페이지로 이동
    }

    @PostMapping(
            value = "/styleupload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public String uploadStyle(
            @RequestParam("images") MultipartFile[] images,
            @RequestParam("style_title") String style_title,
            @RequestParam("caption") String caption,
            @RequestParam("productTag") String productTag,
            @RequestParam("hashtags") String hashtagsJson,
            HttpSession session
    ) throws Exception {
        // ① 비어 있지 않은 파일만 세서 실제 업로드 개수 계산
        int realCount = (int) Arrays.stream(images)
                .filter(mf -> !mf.isEmpty())
                .count();

        // ② hashtags JSON → List<String> 파싱
        List<String> hashtags = objectMapper.readValue(
                hashtagsJson,
                new TypeReference<List<String>>() {}
        );

        // ③ 로그인 유저 ID 추출
        UserBean current = (UserBean) session.getAttribute("currentUser");
        int userId = current.getUser_id();

        List<String> productTags = new ArrayList<String>();
        productTags.add(productTag);

        // ④ 스타일 + 이미지 일괄 저장 호출
        styleService.save(
                style_title,
                realCount,
                images,
                caption,
                productTags,
                hashtags,
                userId
        );
        System.out.println("2323233");
        // ⑤ 업로드 완료 후 마이페이지로 리다이렉트
        return "/Stylepage/Stylepage";
    }
}
