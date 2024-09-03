package net.datasa.sharyproject.controller.Share;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("share")
public class ShareController {

    @GetMapping("main")
    public String main() {

        return "share/main";
    }

    @GetMapping("created")
    public String created() {

        return "share/created";
    }

    @GetMapping("joined")
    public String joined() {

        return "share/joined";
    }
}
