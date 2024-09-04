package net.datasa.sharyproject.controller.share;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("share")
public class ShareController {

    @GetMapping("main")
    public String viewMain() {

        return "share/main";
    }

    @GetMapping("createdDiary")
    public String viewCreatedDiary() {
        return "share/createdDiary";
    }

    @GetMapping("joinedList")
    public String joined() {

        return "share/joinedList";
    }

    @GetMapping("newNote")
    public String newNote() {
        return "share/main";
    }

    @GetMapping("manageDiary")
    public String manageDiary() {

        return "share/manageDiary";
    }

    @GetMapping("deleteDiary")
    public String deleteDiary() {

        return "share/main";
    }

    @PostMapping("withdrawal")
    public String withdrawal() {
        return "share/main";
    }


}
