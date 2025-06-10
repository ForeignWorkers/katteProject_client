package me.soldesk.katte_project_client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class StyleuploadController {

    @GetMapping("/styleupload")
    public String styleUpload() {

        return "redirect:/html/Stylepage/Style_upload.html";
    }
}