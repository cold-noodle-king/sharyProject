package net.datasa.sharyproject.controller.share;

import lombok.AllArgsConstructor;
import net.datasa.sharyproject.domain.dto.personal.CoverTemplateDTO;
import net.datasa.sharyproject.service.personal.CoverTemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("share")
public class ShareController {

    private final CoverTemplateService coverTemplateService;

    //공유 다이어리 메인 페이지로 이동
    //내가 생성한 다이어리 페이지를 디폴트로 설정
    @GetMapping("main")
    public String viewMain() {

        return "share/main";
    }

    //내가 생성한 공유 다이어리로 이동
    @GetMapping("createdDiary")
    public String viewCreatedDiary() {
        return "share/createdDiary";
    }

    //내가 가입한 공유 다이어리 리스트 페이지 출력
    @GetMapping("joinedList")
    public String joined() {

        return "share/joinedList";
    }

    //내가 가입한 공유 다어리로 이동
    @GetMapping("joinedDiary")
    public String joinedDiary() {
        return "share/JoinedDiary";
    }

    @GetMapping("categorySelect")
    public String categorySelect() {
        return "share/CategorySelect";
    }

    @GetMapping("categoryUpdate")
    public String categoryUpdate() {
        return "share/CategoryUpdate";
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

        return "share/InfoUpdate";
    }

    @GetMapping("viewMember")
    public String viewMember() {
        return "share/ViewMember";
    }

    @GetMapping("memberList")
    public String memberList() {
        return "share/MemberList";
    }

    @GetMapping("registerRequest")
    public String registerRequest() {
        return "share/RegisterRequest";
    }

    @GetMapping("getCoverTemplates")
    @ResponseBody
    public List<CoverTemplateDTO> getCoverTemplates() {
        return coverTemplateService.getCoverTemplates(); // 커버 템플릿 리스트 반환
    }

}
