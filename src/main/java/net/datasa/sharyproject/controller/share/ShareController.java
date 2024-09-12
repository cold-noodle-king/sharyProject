package net.datasa.sharyproject.controller.share;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.personal.CategoryDTO;
import net.datasa.sharyproject.domain.dto.personal.CoverTemplateDTO;
import net.datasa.sharyproject.domain.dto.personal.NoteTemplateDTO;
import net.datasa.sharyproject.domain.dto.share.ShareDiaryDTO;
import net.datasa.sharyproject.security.AuthenticatedUser;
import net.datasa.sharyproject.service.personal.CoverTemplateService;
import net.datasa.sharyproject.service.personal.NoteTemplateService;
import net.datasa.sharyproject.service.share.ShareDiaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping("share")
public class ShareController {

    private final CoverTemplateService coverTemplateService;
    private final NoteTemplateService noteTemplateService;
    private final ShareDiaryService shareDiaryService;

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

    //리스트 형태로 받아온 카테고리를 문자열로 변환하는 메서드
    public class CategoryUtil {

        // 리스트를 쉼표로 구분된 문자열로 변환
        public static String listToString(List<String> categories) {
            return String.join(", ", categories);  // 쉼표와 공백으로 구분하여 문자열 생성
        }
    }

    @PostMapping("categorySave")
    public String categorySave(@RequestParam("categories") List<String> categories, Model model) {
        log.debug("지정한 카테고리: {}", categories);

        String categoryName = CategoryUtil.listToString(categories);
        log.debug("문자열로 변환한 카테고리 이름:{}", categoryName);

        model.addAttribute("categoryName", categoryName);

        return "share/CoverSelect";
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

    //다이어리를 DB에 저장하는 메서드
    @PostMapping("saveDiary")
    public String saveDiary(@ModelAttribute ShareDiaryDTO shareDiaryDTO
                          ,@AuthenticationPrincipal AuthenticatedUser user
                          ,RedirectAttributes redirectAttributes){
        log.debug("컨틀롤러로 갔는지 확인:{}", shareDiaryDTO);

        shareDiaryService.saveDiary(shareDiaryDTO, user);

        return "redirect:/share/main";
    }

    //새로운 노트 추가 페이지로 이동
    @GetMapping("newNote")
    public String newNote() {

        return "share/main";
    }

    //다이어리 관리 페이지로 이동
    @GetMapping("manageDiary")
    public String manageDiary() {

        return "share/manageDiary";
    }

    //내가 생성한 공유다이어리를 삭제하는 메서드
    @GetMapping("deleteDiary")
    public String deleteDiary() {

        return "share/main";
    }

    //가입한 공유다이어리를 탈퇴하는 메서드
    @PostMapping("withdrawal")
    public String withdrawal() {
        
        return "share/main";
    }

    //공유다이어리 정보 수정 페이지 출력
    @GetMapping("infoUpdate")
    public String infoUpdate() {

        return "share/InfoUpdate";
    }

    //공유 다이어리 멤버 관리 페이지
    @GetMapping("viewMember")
    public String viewMember() {
        return "share/ViewMember";
    }

    //공유 다이어리 멤버 리스트 출력
    @GetMapping("memberList")
    public String memberList() {
        return "share/MemberList";
    }

    //공유 다이어리 가입 요청 리스트 출력
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
