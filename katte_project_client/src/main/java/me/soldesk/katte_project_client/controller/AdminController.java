package me.soldesk.katte_project_client.controller;



import common.bean.user.UserRestrictionBean;
import me.soldesk.katte_project_client.service.AdminUserListService;
import me.soldesk.katte_project_client.service.InspectionListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import common.bean.admin.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private InspectionListService inspectionListService;

    @Autowired
    private AdminUserListService adminUserListService;

    //관리자 회원 관리 페이지(디폴트)로 이동
    @GetMapping("/admin")
    public String adminMain() {
        return "redirect:/admin/user_view";
    }

    //회원 관리 탭
    @GetMapping("/admin/user_view")
    public String showUserManagementPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String filter,
            Model model
    ) {
        if (page < 1) page = 1;
        if (size < 1) size = 20;

        int offset = (page - 1) * size;

        List<UserAdminViewBean> userList = adminUserListService.getUserList(offset, size, filter);

        int totalCount = adminUserListService.getUserTotalCount(filter); //filter 적용된 count
        int totalPages = Math.max(1, (int) Math.ceil((double) totalCount / size));


        model.addAttribute("userList", userList);
        model.addAttribute("userCount", totalCount); //총 회원 수
        model.addAttribute("memberCurrentPage", page);
        model.addAttribute("memberTotalPages", totalPages); //페이징 처리
        model.addAttribute("activeTab", "member-area"); // 탭 id

        System.out.println("➡️ userList size: " + (userList != null ? userList.size() : "null"));
        System.out.println("➡️ userCount: " + totalCount);

        //viewToggle을 위해 filter 값을 그대로 넘김
        model.addAttribute("filter", filter);

        return "Membership_management/Membership_management";
    }

    //검수 관리 탭
    @GetMapping("/admin/inspection_view")
    public String showInspectionPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        if (page < 1) page = 1;
        if (size < 1) size = 10;

        int offset = (page - 1) * size;

        List<InspectionProductViewBean> list = inspectionListService.getInspectionItems(offset, size);

        // 🔍 여기서 check_step 로그 찍기 (확인할 것 1)
        for (InspectionProductViewBean item : list) {
            System.out.println("🟨 check_result_id: " + item.getCheck_result_id() +
                    " | check_step: " + (item.getCheck_step() != null ? item.getCheck_step().name() : "NULL"));
        }

        int totalCount = inspectionListService.getInspectionTotalCount();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalCount / size));

        model.addAttribute("inspectionList", list);
        model.addAttribute("inspectionCount", totalCount);
        model.addAttribute("reviewCurrentPage", page);
        model.addAttribute("reviewTotalPages", totalPages);

        model.addAttribute("activeTab", "review-area");


        //공통 페이징 항목들도 null 방지를 위해 추가
        model.addAttribute("memberTotalPages", 1);
        model.addAttribute("memberCurrentPage", 1);

        return "Membership_management/Membership_management";
    }

    //회원 검색
    @GetMapping("/admin/user_search")
    public String searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model
    ) {
        List<UserAdminViewBean> searchResult = adminUserListService.searchUserList(keyword, page, size);

        int totalCount = adminUserListService.searchUserCount(keyword);
        int totalPages = Math.max(1, (int) Math.ceil((double) totalCount / size));


        model.addAttribute("userList", searchResult);
        model.addAttribute("userCount", totalCount);
        model.addAttribute("memberCurrentPage", page);
        model.addAttribute("memberTotalPages", totalPages);// 페이지 수 계산하려면 total count API 필요
        model.addAttribute("activeTab", "member-area");


        return "Membership_management/Membership_management";
    }

    //---------------------------------------- 회원 상태 처리 ---------------------------------------------------------//
    // 상태 관리 중앙 컨트롤러
    @PostMapping("/admin/change_status")
    public String changeUserStatus(
            @RequestParam("user_id") int userId,
            @RequestParam("action_type") String actionType,
            @RequestParam(value = "stop_days", required = false) Integer stopDays,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        adminUserListService.changeUserStatus(userId, actionType, stopDays);

        if (keyword != null && !keyword.isEmpty()) {
            return "redirect:/admin/user_search?page=" + page + "&keyword=" + keyword;
        } else {
            return "redirect:/admin/user_view?page=" + page;
        }
    }

    @GetMapping("/restriction/comment")
    @ResponseBody
    public List<UserRestrictionBean> getCommentRestrictions(@RequestParam("user_id") int userId) {
        return adminUserListService.getUserRestrictions(userId);
    }
}
