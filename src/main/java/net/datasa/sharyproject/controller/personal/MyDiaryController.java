package net.datasa.sharyproject.controller.personal;

import lombok.RequiredArgsConstructor;
import net.datasa.sharyproject.domain.dto.personal.CoverTemplateDTO;
import net.datasa.sharyproject.domain.dto.personal.NoteTemplateDTO;
import net.datasa.sharyproject.domain.dto.personal.PersonalDiaryDTO;
import net.datasa.sharyproject.service.personal.CoverTemplateService;
import net.datasa.sharyproject.service.personal.NoteTemplateService;
import net.datasa.sharyproject.service.personal.PersonalDiaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 개인 다이어리와 관련된 요청을 처리하는 컨트롤러입니다.
 */
@RequiredArgsConstructor
@RequestMapping("personal")
@Controller
public class MyDiaryController {

    private final CoverTemplateService coverTemplateService; // 커버 템플릿 서비스
    private final NoteTemplateService noteTemplateService; // 노트 템플릿 서비스
    private final PersonalDiaryService personalDiaryService; // 개인 다이어리 서비스

    /**
     * MyDiary 메인 페이지로 이동하는 메서드
     * @return MyDiary 페이지
     */
    @GetMapping("MyDiary")
    public String MyDiary() {
        return "personal/MyDiary";  // MyDiary 페이지로 이동
    }

    /**
     * 카테고리 선택 페이지로 이동하는 메서드
     * @return 카테고리 선택 페이지
     */
    @GetMapping("categorySelect")
    public String categorySelect() {
        return "personal/PersonalCategorySelect";  // 카테고리 선택 페이지로 이동
    }

    /**
     * 커버 선택 페이지로 이동하는 메서드
     * @return 커버 선택 페이지
     */
    @GetMapping("cover")
    public String cover() {
        return "personal/CoverSelect";  // 커버 선택 페이지로 이동
    }

    /**
     * 커버 템플릿 데이터를 반환하는 API
     * @return 커버 템플릿 DTO 리스트
     */
    @GetMapping("getCoverTemplates")
    @ResponseBody
    public List<CoverTemplateDTO> getCoverTemplates() {
        return coverTemplateService.getCoverTemplates(); // 커버 템플릿 리스트 반환
    }

    /**
     * 노트 선택 페이지로 이동하는 메서드
     * @return 노트 선택 페이지
     */
    @GetMapping("note")
    public String note() {
        return "personal/NoteSelect";  // 노트 템플릿 선택 페이지로 이동
    }

    /**
     * 노트 템플릿 데이터를 반환하는 API
     * @return 노트 템플릿 DTO 리스트
     */
    @GetMapping("getNoteTemplates")
    @ResponseBody
    public List<NoteTemplateDTO> getNoteTemplates() {
        return noteTemplateService.getNoteTemplates();  // 노트 템플릿 리스트 반환
    }

    /**
     * 다이어리 제목과 커버를 저장하는 메서드
     * @param diaryDTO 다이어리 정보가 포함된 DTO
     * @return 저장 결과에 따른 응답
     */
    @PostMapping("diary/save")
    @ResponseBody
    public ResponseEntity<String> saveDiary(@RequestBody PersonalDiaryDTO diaryDTO) {
        try {
            personalDiaryService.saveDiary(diaryDTO);  // 다이어리 저장 로직 호출
            return ResponseEntity.ok("다이어리가 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("다이어리 저장에 실패했습니다.");
        }
    }

    /**
     * 노트 템플릿을 기반으로 다이어리 작성 페이지로 이동하는 메서드
     * @param noteNum 노트 템플릿 ID
     * @param model 모델 객체에 노트 템플릿 데이터를 추가
     * @return 다이어리 작성 페이지
     */
    @GetMapping("noteForm")
    public String createDiary(@RequestParam("noteNum") Integer noteNum, Model model) {
        NoteTemplateDTO noteTemplate = noteTemplateService.getNoteTemplateById(noteNum);

        // 노트 템플릿이 없거나 이미지가 없는 경우 예외 발생
        if (noteTemplate == null || noteTemplate.getNoteImage() == null) {
            throw new RuntimeException("NoteTemplate 또는 이미지 경로가 존재하지 않습니다.");
        }

        model.addAttribute("noteTemplate", noteTemplate);  // 모델에 노트 템플릿 데이터를 추가
        return "personal/NoteForm";  // 다이어리 작성 페이지로 이동
    }
}