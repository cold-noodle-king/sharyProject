package net.datasa.sharyproject.controller.share;

import lombok.AllArgsConstructor;
import net.datasa.sharyproject.domain.dto.personal.CoverTemplateDTO;
import net.datasa.sharyproject.service.cover.CoverTemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("share")
public class ShareController {

    private final CoverTemplateService coverTemplateService;

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

    @GetMapping("joinedDiary")
    public String joinedDiary() {
        return "share/joinedDiary";
    }

    @GetMapping("categorySelect")
    public String categorySelect() {
        return "share/CategorySelect";
    }

    @GetMapping("cover")
    public String cover() {
        return "share/CoverSelect";
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

    @GetMapping("infoUpdate")
    public String infoUpdate() {

        return "share/infoUpdate";
    }

    @GetMapping("viewMember")
    public String viewMember() {
        return "share/viewMember";
    }

    @GetMapping("getCoverTemplates")
    @ResponseBody
    public List<CoverTemplateDTO> getCoverTemplates() {
        return coverTemplateService.getCoverTemplates(); // 커버 템플릿 리스트 반환
    }

}
