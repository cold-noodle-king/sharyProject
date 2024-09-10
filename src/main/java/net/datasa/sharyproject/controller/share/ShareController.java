package net.datasa.sharyproject.controller.share;

import lombok.AllArgsConstructor;
import net.datasa.sharyproject.domain.dto.personal.CoverTemplateDTO;
import net.datasa.sharyproject.domain.dto.personal.NoteTemplateDTO;
import net.datasa.sharyproject.service.personal.CoverTemplateService;
import net.datasa.sharyproject.service.personal.NoteTemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("share")
public class ShareController {

    private final CoverTemplateService coverTemplateService;
    private final NoteTemplateService noteTemplateService;

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

    //내가 가입한 공유 다이어리로 이동
    @GetMapping("joinedDiary")
    public String joinedDiary() {
        return "share/JoinedDiary";
    }

    //다이어리 카테고리 선택 페이지로 이동
    @GetMapping("categorySelect")
    public String categorySelect() {
        return "share/CategorySelect";
    }

    //다이어리 카테고리 수정 페이지로 이동
    @GetMapping("categoryUpdate")
    public String categoryUpdate() {
        return "share/CategoryUpdate";
    }

    //다이어리 커버 페이지로 이동
    @GetMapping("cover")
    public String cover() {
        return "share/CoverSelect";
    }

    //새로운 노트 추가 페이지로 이동
    @GetMapping("newNote")
    public String newNote() {

        return "share/main";
    }

    //
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

    // 노트 선택 페이지로 이동하는 메서드 추가
    @GetMapping("note")
    public String note() {
        return "share/NoteSelect";  // 노트 템플릿 선택 페이지로 이동
    }

    // 노트 템플릿 데이터를 제공하는 API
    @GetMapping("getNoteTemplates")
    @ResponseBody
    public List<NoteTemplateDTO> getNoteTemplates() {
        return noteTemplateService.getNoteTemplates();  // 노트 템플릿 리스트 반환
    }

    @GetMapping("noteForm")
    public String createDiary(@RequestParam("noteNum") Integer noteNum, Model model) {
        NoteTemplateDTO noteTemplate = noteTemplateService.getNoteTemplateById(noteNum);

        if (noteTemplate == null || noteTemplate.getNoteImage() == null) {
            throw new RuntimeException("NoteTemplate 또는 이미지 경로가 존재하지 않습니다.");
        }

        model.addAttribute("noteTemplate", noteTemplate);
        return "share/NoteForm";  // 다이어리 작성 페이지로 이동
    }


}
