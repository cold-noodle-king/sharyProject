package net.datasa.sharyproject.controller.personal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.personal.CoverTemplateDTO;
import net.datasa.sharyproject.domain.dto.personal.NoteTemplateDTO;
import net.datasa.sharyproject.domain.dto.personal.PersonalDiaryDTO;
import net.datasa.sharyproject.service.personal.CoverTemplateService;
import net.datasa.sharyproject.service.personal.NoteTemplateService;
import net.datasa.sharyproject.service.personal.PersonalDiaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
@Slf4j
public class MyDiaryController {

    private final CoverTemplateService coverTemplateService; // 커버 템플릿 서비스
    private final NoteTemplateService noteTemplateService; // 노트 템플릿 서비스
    private final PersonalDiaryService personalDiaryService; // 개인 다이어리 서비스

    /**
     * MyDiary 메인 페이지로 이동하는 메서드
     * @return MyDiary 페이지
     */
    @GetMapping("/MyDiary")
    public String getMyDiaryList(Model model) {
        // 로그인된 사용자의 다이어리 목록을 조회
        List<PersonalDiaryDTO> diaryList = personalDiaryService.getDiariesByLoggedInMember();

        // 다이어리 목록을 모델에 추가하여 뷰로 전달
        model.addAttribute("diaryList", diaryList);

        return "personal/MyDiary"; // MyDiary 페이지로 이동
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
     * 카테고리 저장 후 커버 선택 페이지로 리디렉션하는 메서드
     * @param categoryNum 선택된 카테고리
     * @param model 카테고리 정보를 모델에 저장
     * @return 커버 선택 페이지로 리디렉션
     */
    @PostMapping("categorySave")
    public String categorySave(@RequestParam("categoryNum") Integer categoryNum, Model model) {
        // 선택된 카테고리 정보를 모델에 추가
        model.addAttribute("categoryNum", categoryNum);

        // 커버 템플릿 선택 페이지로 리디렉션
        return "redirect:/personal/cover?categoryNum=" + categoryNum; // 쿼리 파라미터로 전달
    }

    /**
     * 커버 선택 페이지로 이동하는 메서드
     * @return 커버 선택 페이지
     */
    @GetMapping("cover")
    public String cover(@RequestParam(name = "categoryNum", required = false) Integer categoryNum, Model model) {
        model.addAttribute("categoryNum", categoryNum); // 카테고리 번호를 모델에 추가
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
     * 다이어리 제목과 커버를 저장하는 메서드 (로그인한 사용자 정보 활용)
     * @param diaryName 다이어리 제목
     * @param coverTemplateNum 커버 템플릿 번호
     * @param categoryNum 카테고리 번호
     * @param loggedInUser 로그인된 사용자 정보
     * @return 저장 결과에 따른 응답
     */
    @PostMapping("saveDiary")
    @ResponseBody
    public ResponseEntity<String> saveDiary(@RequestParam String diaryName,
                                            @RequestParam Integer coverTemplateNum,
                                            @RequestParam Integer categoryNum,
                                            @AuthenticationPrincipal UserDetails loggedInUser) {
        try {
            // PersonalDiaryDTO에 받은 데이터를 설정
            PersonalDiaryDTO diaryDTO = new PersonalDiaryDTO();
            diaryDTO.setDiaryName(diaryName);
            diaryDTO.setCoverNum(coverTemplateNum);
            diaryDTO.setCategoryNum(categoryNum);
            diaryDTO.setMemberId(loggedInUser.getUsername()); // 로그인된 사용자 ID

            // 로그로 값을 출력하여 확인
            log.debug("Diary Name: {}", diaryName);
            log.debug("Cover Template Num: {}", coverTemplateNum);
            log.debug("Category Num: {}", categoryNum); // 이 로그를 통해 categoryNum을 확인

            // 다이어리 저장 로직 호출
            personalDiaryService.saveDiary(diaryDTO);

            return ResponseEntity.ok("다이어리가 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            log.error("다이어리 저장 실패", e);
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