package net.datasa.sharyproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping({"/",""})   //info 컨트롤러와 경로 바꾸기 ("home")
    public String home() {
        return "home";
    }

    @GetMapping("/emotion-popup")
    public String emotion_popup() {
        return "fragments/emotion_popup";
    }

}