package net.datasa.sharyproject.Controller.Share;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("share")
public class ShareController {

    @GetMapping("main")
    public String main() {

        return "share/main";
    }
}
