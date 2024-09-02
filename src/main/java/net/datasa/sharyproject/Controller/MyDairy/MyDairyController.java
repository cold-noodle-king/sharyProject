package net.datasa.sharyproject.Controller.MyDairy;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("personal")
@Controller
public class MyDairyController {

    @GetMapping("MyDairy")
    public String MyDairy() {
        return "personal/MyDairy";
    }
}
