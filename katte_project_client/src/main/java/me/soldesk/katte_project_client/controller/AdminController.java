package me.soldesk.katte_project_client.controller;



import me.soldesk.katte_project_client.service.InspectionListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import common.bean.admin.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AdminController {
    @Autowired
    private InspectionListService inspectionListService;

    //관리자 회원 관리 페이지(디폴트)로 이동
    @GetMapping("/admin")
    public String adminMain() {
        return "redirect:/admin/inspection-view";
    }

    @GetMapping("/admin/inspection-view")
    public String showInspectionPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        // ✅ 방어 코드 추가
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        int offset = (page - 1) * size;

        List<InspectionProductViewBean> list = inspectionListService.getInspectionItems(offset, size);
        int totalCount = inspectionListService.getInspectionTotalCount();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalCount / size));

        model.addAttribute("inspectionList", list);
        model.addAttribute("inspectionCount", totalCount);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("activeTab", "review-area");

        return "Membership_management/Membership_management";
    }

}
