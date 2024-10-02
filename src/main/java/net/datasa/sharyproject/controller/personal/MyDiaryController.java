package net.datasa.sharyproject.controller.personal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.CategoryDTO;
import net.datasa.sharyproject.domain.dto.EmotionDTO;
import net.datasa.sharyproject.domain.dto.HashtagDTO;
import net.datasa.sharyproject.domain.dto.personal.*;
import net.datasa.sharyproject.domain.entity.CategoryEntity;
import net.datasa.sharyproject.service.EmotionService;
import net.datasa.sharyproject.service.HashtagService;
import net.datasa.sharyproject.service.personal.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("personal")
@Controller
@Slf4j
public class MyDiaryController {

    private final PersonalDiaryService personalDiaryService; // 개인 다이어리 서비스
    private final PersonalNoteService personalNoteService;   // 개인 노트 서비스
    private final CoverTemplateService coverTemplateService;
    private final NoteTemplateService noteTemplateService;
    private final EmotionService emotionService;
    private final GrantedService grantedService;
    private final HashtagService hashtagService;

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
     * 다이어리 상세 페이지 (myNote)로 이동하는 메서드
     * @param diaryNum 선택된 다이어리 번호
     * @param model 다이어리 정보와 노트 목록을 모델에 추가
     * @return MyNote 페이지
     */
    @GetMapping("/MyNote/{diaryNum}")
    public String viewDiaryNotes(@PathVariable("diaryNum") Integer diaryNum, Model model) {
        // 선택된 다이어리 정보를 가져오기
        PersonalDiaryDTO selectedDiary = personalDiaryService.getDiaryById(diaryNum);
        model.addAttribute("selectedDiary", selectedDiary);

        // 선택된 다이어리의 노트 목록을 가져오기
        List<PersonalNoteDTO> noteList = personalNoteService.getNotesByDiaryNum(diaryNum);
        model.addAttribute("noteList", noteList);

        // diaryNum을 모델에 추가
        model.addAttribute("diaryNum", diaryNum);

        return "personal/MyNote"; // MyNote 페이지로 이동
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
    public String note(@RequestParam(value = "diaryNum", required = false) Integer diaryNum, Model model) {
        if (diaryNum == null) {
            throw new RuntimeException("Diary number is missing");
        }
        model.addAttribute("diaryNum", diaryNum);
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
    public ResponseEntity<Map<String, Object>> saveDiary(@RequestParam String diaryName,
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

            // 다이어리 저장 후 생성된 다이어리 번호 반환
            Integer savedDiaryNum = personalDiaryService.saveDiary(diaryDTO);

            // 응답에 diaryNum을 포함하여 반환
            Map<String, Object> response = new HashMap<>();
            response.put("message", "다이어리가 성공적으로 저장되었습니다.");
            response.put("diaryNum", savedDiaryNum);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("다이어리 저장 실패", e);
            return ResponseEntity.status(500).body(Map.of("error", "다이어리 저장에 실패했습니다."));
        }
    }

    // 수정/삭제 페이지로 이동
    @GetMapping("/editOrDeleteDiary/{diaryNum}")
    public String editOrDeleteDiary(@PathVariable("diaryNum") Integer diaryNum, Model model) {
        // 선택된 다이어리 정보 가져오기
        PersonalDiaryDTO diary = personalDiaryService.getDiaryById(diaryNum);
        model.addAttribute("diary", diary);

        // 카테고리 및 커버 리스트 가져오기
        List<CategoryEntity> categories = personalDiaryService.getAllCategories();
        List<CoverTemplateDTO> covers = coverTemplateService.getCoverTemplates();
        model.addAttribute("categories", categories);
        model.addAttribute("covers", covers);

        return "personal/editOrDeleteDiary";
    }

    // 다이어리 수정 처리
    @PostMapping("/updateDiary")
    public ResponseEntity<?> updateDiary(@RequestParam Integer diaryNum,  // 다이어리 ID
                                         @RequestParam String diaryName,  // 다이어리 이름
                                         @RequestParam Integer categoryNum,  // 카테고리 ID
                                         @RequestParam Integer coverNum) {  // 커버 ID
        try {
            // 수정할 다이어리 정보 가져오기
            PersonalDiaryDTO diaryDTO = personalDiaryService.getDiaryById(diaryNum);
            diaryDTO.setDiaryName(diaryName);  // 다이어리 이름 업데이트
            diaryDTO.setCategoryNum(categoryNum);  // 카테고리 업데이트
            diaryDTO.setCoverNum(coverNum);  // 커버 업데이트

            personalDiaryService.updateDiary(diaryDTO);  // 다이어리 정보 저장
            return ResponseEntity.ok("다이어리 수정 성공");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("다이어리 수정 실패");
        }
    }

    // 다이어리 삭제 처리 (POST)
    @PostMapping("/deleteDiary")
    public ResponseEntity<?> deleteDiary(@RequestParam Integer diaryNum) {  // 다이어리 ID
        try {
            personalDiaryService.deleteDiary(diaryNum);  // 다이어리 삭제
            return ResponseEntity.ok("다이어리 삭제 성공");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("다이어리 삭제 실패");
        }
    }

}