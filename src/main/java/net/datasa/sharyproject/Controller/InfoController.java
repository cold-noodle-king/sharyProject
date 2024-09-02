package net.datasa.sharyproject.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InfoController {
    @GetMapping({"", "/"})
    public String info() {

        return "home"; // 나중에 info페이지 만들때 경로바꾸기
    }
}


