package net.datasa.sharyproject.controller.personal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("personal")
@Controller
public class MyDiaryController {

    @GetMapping("MyDiary")
    public String MyDiary() {
        return "personal/MyDiary";
    }

    @GetMapping("categorySelect")
    public String categorySelect() {
        return "personal/PersonalCategorySelect";
    }
}