package me.soldesk.katte_project_client.controller;

import common.bean.cs.CsAnnounceBean;
import common.bean.cs.CsFaqBean;
import common.bean.cs.CsStandardBean;
import me.soldesk.katte_project_client.service.CsCenterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class CsCenterController {

    private final CsCenterService csCenterService;

    public CsCenterController(CsCenterService csCenterService) {
        this.csCenterService = csCenterService;
    }

/* ───────────────────────────────────────────────────────────────────────────
                                     공지사항
   ─────────────────────────────────────────────────────────────────────────── */

    @GetMapping("/announce")
    public String showAnnounceList(
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(defaultValue = "0") int offset,
            Model model) {
        System.out.println("공지 리스트 컨트롤러 탔어요: count=" + count + ", offset=" + offset);
        List<CsAnnounceBean> announces = csCenterService.getCsAnnounceBean(count, offset);
        System.out.println("받은 공지 수: " + announces.size());
        model.addAttribute("announces", announces);
        return "CsCenter/Cs_Announce";
    }

    @GetMapping("/announce/post")
    public String showAnnouncePost() {
        System.out.println("공지사항 작성하기 컨트롤러 탔어요");
        return "CsCenter/Cs_Announce_Post";
    }

    @PostMapping("/announce/add")
    public String addAnnounce(@ModelAttribute CsAnnounceBean bean) {
        System.out.println("공지 추가 컨트롤러 탔어요");
        System.out.println("받은 카테고리: " + bean.getAnnounce_category());
        System.out.println("받은 제목: " + bean.getAnnounce_title());
        System.out.println("받은 내용: " + bean.getAnnounce_content());

        csCenterService.postAnnounce(bean);
        return "redirect:/announce";
    }

    @GetMapping("/announce/edit/{announce_id}")
    public String showAnnounceDetail(@PathVariable("announce_id") int announce_id, Model model) {
        System.out.println("단일 정보 컨트롤러 탔어요");
        CsAnnounceBean announce = csCenterService.getCsAnnounceBean(announce_id);
        model.addAttribute("announce", announce);
        return "CsCenter/Cs_Announce_Edit";
    }

    @PostMapping("/announce/edit")
    public String editAnnounce(@ModelAttribute CsAnnounceBean bean) {
        System.out.println("공지 수정 컨트롤러 탔어요");
        System.out.println("수정된 카테고리: " + bean.getAnnounce_category());
        System.out.println("수정된 제목: " + bean.getAnnounce_title());
        System.out.println("수정된 내용: " + bean.getAnnounce_content());

        csCenterService.updateAnnounce(bean);
        return "redirect:/announce";
    }


    @PostMapping("/announce/delete")
    public String deleteAnnounce(@RequestParam("announce_id") int announce_id) {
        System.out.println("공지 삭제 컨트롤러 탔어요");
        csCenterService.deleteAnnounce(announce_id);
        return "redirect:/announce";
    }

 /* ───────────────────────────────────────────────────────────────────────────
                                   자주 묻는 질문
   ─────────────────────────────────────────────────────────────────────────── */


    @GetMapping("/faq")
    public String showFaqList(
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(defaultValue = "0") int offset,
            Model model) {
        System.out.println("FAQ 리스트 컨트롤러 탔어요: count=" + count + ", offset=" + offset);
        List<CsFaqBean> faqs = csCenterService.getCsFaqBean(count, offset);
        model.addAttribute("faqs", faqs);
        return "CsCenter/Cs_Faq";
    }

    @GetMapping("/faq/post")
    public String showFaqPost() {
        System.out.println("faq 작성하기 컨트롤러 탔어요");
        return "CsCenter/Cs_Faq_Post";
    }

    @PostMapping("/faq/add")
    public String addFaq(@ModelAttribute CsFaqBean bean) {
        System.out.println("공지 추가 컨트롤러 탔어요");
        System.out.println("받은 카테고리: " + bean.getFaq_category());
        System.out.println("받은 제목: " + bean.getFaq_title());
        System.out.println("받은 내용: " + bean.getFaq_content());

        csCenterService.postFaq(bean);
        return "redirect:/faq";
    }


    @GetMapping("/faq/edit/{faq_id}")
    public String showFaqDetail(@PathVariable("faq_id") int faq_id, Model model) {
        System.out.println("단일 정보 컨트롤러 탔어요");
        CsFaqBean faq = csCenterService.getCsFaqBean(faq_id);
        model.addAttribute("faq", faq);
        return "CsCenter/Cs_Faq_Edit";
    }

    @PostMapping("/faq/edit")
    public String editFaq(@ModelAttribute CsFaqBean bean) {
        System.out.println("faq 수정 컨트롤러 탔어요");
        System.out.println("수정된 카테고리: " + bean.getFaq_category());
        System.out.println("수정된 제목: " + bean.getFaq_title());
        System.out.println("수정된 내용: " + bean.getFaq_content());

        csCenterService.updateFaq(bean);
        return "redirect:/faq";
    }


    @PostMapping("/faq/delete")
    public String deleteFaq(@RequestParam("faq_id") int faq_id) {
        System.out.println("faq 삭제 컨트롤러 탔어요");
        csCenterService.deleteFaq(faq_id);
        return "redirect:/faq";
    }

/* ───────────────────────────────────────────────────────────────────────────
                                    1:1 문의
   ─────────────────────────────────────────────────────────────────────────── */


    /*@GetMapping("/inquiry/{user_id}")
    public String showInquireCustomerList(
            @PathVariable("user_id") int user_id,
            @RequestParam(value = "count", defaultValue = "10") int count,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            Model model
    ) {
        System.out.println("문의 리스트 컨트롤러 탔어요");

        List<CsInquireCustomerBean> inquires = csCenterService.getCsInquireCustomerBean(user_id, count, offset);

        System.out.println("받은 문의 수: " + inquires.size());
        model.addAttribute("inquires", inquires);

        return "CsCenter/Cs_Inquire_Customer";
    }

    @PostMapping("/inquiry/post")
    public String addInquireCustomer(@ModelAttribute CsInquireCustomerBean bean) {
        System.out.println("문의 작성 컨트롤러 탔어요");
        System.out.println("user_id = " + bean.getUser_id());
        System.out.println("받은 카테고리: " + bean.getInquire_category());
        System.out.println("받은 제목: " + bean.getInquire_title());
        System.out.println("받은 내용: " + bean.getInquire_content());

        csCenterService.postInquire_Customer(bean);
        return "redirect:/Cs_Inquire_Customer";
    }


    @GetMapping("/Inquire_Admin")
    public String csInquireAdmin() {
        return "CsCenter/Cs_Inquire_Admin";
    }*/

    @GetMapping("/Inquire_Customer")
    public String csInquireCustomer() {
        return "CsCenter/Cs_Inquire_X";
    }

    /* ───────────────────────────────────────────────────────────────────────────
                                   검수 기준
   ─────────────────────────────────────────────────────────────────────────── */

    @GetMapping("/standard")
    public String getStandardPage(
            @RequestParam(name = "standard_category", defaultValue = "SHOES") String standard_category,
            Model model) {
        System.out.println("standard 리스트 컨트롤러 탔어요");
        List<CsStandardBean> standards = csCenterService.getStandardsByCategory(standard_category.toUpperCase());

        System.out.println("받은 검수 기준의 갯수 : " + standards.size());

        model.addAttribute("csStandardBean", standards);
        model.addAttribute("selectedCategory", standard_category.toUpperCase());

        return "CsCenter/Cs_Standard";
    }

    @GetMapping("/standard/post")
    public String showStandardPost() {
        System.out.println("검수 기준 작성하기 컨트롤러 탔어요");
        return "CsCenter/Cs_Standard_Post";
    }

    @PostMapping("/standard/add")
    public String addStandard(@ModelAttribute CsStandardBean bean) {
        System.out.println("검수 기준 작성 컨트롤러 탔어요");
        System.out.println("받은 카테고리: " + bean.getStandard_category());
        System.out.println("받은 제목: " + bean.getStandard_content());

        csCenterService.postStandard(bean);
        return "redirect:/standard";
    }


    @GetMapping("/standard/edit/{standard_id}")
    public String showStandardDetail(
            @PathVariable("standard_id") int standard_id, Model model) {
        System.out.println("검수 기준 단일 정보 컨트롤러 탔어요");
        CsStandardBean csStandardBean = csCenterService.getStandardById(standard_id);
        System.out.println("받은 id : " + csStandardBean.getStandard_id());
        System.out.println("받은 컨텐츠 : " + csStandardBean.getStandard_category());
        System.out.println("받은 컨텐츠 : " + csStandardBean.getStandard_content());

        model.addAttribute("csStandardBean", csStandardBean);

        return "CsCenter/Cs_Standard_Edit";
    }

    @PostMapping("/standard/edit")
    public String editStandard(@ModelAttribute CsStandardBean bean) {
        System.out.println("검수 기준 수정 컨트롤러 탔어요");
        System.out.println("수정된 카테고리: " + bean.getStandard_category());
        System.out.println("수정된 내용: " + bean.getStandard_content());

        csCenterService.editStandard(bean);
        return "redirect:/standard";
    }


    @PostMapping("/standard/delete")
    public String deleteStandard(
            @RequestParam("standard_id") int standard_id) {
        System.out.println("검수 기준 삭제 컨트롤러 탔어요");
        csCenterService.deleteStandard(standard_id);
        return "redirect:/standard";
    }
}