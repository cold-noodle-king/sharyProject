package net.datasa.sharyproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InfoController {
    @GetMapping({"info"})   //나중에 html 컨트롤러랑 경로 바꾸기
    public String info() {

        return "info";
    }


}


