package net.datasa.sharyproject.Controller.Share;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("share")
public class ShareController {

    @GetMapping("main")
    public String viewMain() {

        return "share/main";
    }

    @GetMapping("joinedList")
    public String joined() {

        return "share/joinedList";
    }

    @GetMapping("newNote")
    public String newNote() {
        return "share/main";
    }

    @GetMapping("editDiary")
    public String editDiary() {

        return "share/editDiary";
    }

    @PostMapping("deleteDiary")
    public String deleteDiary() {

        return "share/main";
    }

    @PostMapping("withdrawal")
    public String withdrawal() {
        return "share/main";
    }


}
