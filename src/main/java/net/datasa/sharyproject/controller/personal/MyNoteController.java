package net.datasa.sharyproject.controller.personal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.EmotionDTO;
import net.datasa.sharyproject.domain.dto.HashtagDTO;
import net.datasa.sharyproject.domain.dto.personal.*;
import net.datasa.sharyproject.service.EmotionService;
import net.datasa.sharyproject.service.HashtagService;
import net.datasa.sharyproject.service.personal.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@Controller
@RequestMapping("personal")
@RequiredArgsConstructor
@Slf4j
public class MyNoteController {

    private final PersonalNoteService personalNoteService;
    private final NoteTemplateService noteTemplateService;
    private final PersonalDiaryService personalDiaryService;
    private final EmotionService emotionService;
    private final GrantedService grantedService;
    private final HashtagService hashtagService;

    // 파일 업로드 디렉토리 설정 (application.properties에서 주입)
    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 노트 템플릿을 기반으로 다이어리 작성 페이지로 이동하는 메서드
     *
     * @param noteNum  노트 번호
     * @param diaryNum 다이어리 번호
     * @param noteName 노트 이름
     * @param model    모델 객체
     * @return 다이어리 작성 페이지 뷰 이름
     */
    @GetMapping("noteForm")
    public String createDiary(@RequestParam("noteNum") Integer noteNum,
                              @RequestParam("diaryNum") Integer diaryNum,
                              @RequestParam("noteName") String noteName,
                              Model model) {
        // 노트 템플릿 정보 가져오기
        NoteTemplateDTO noteTemplate = noteTemplateService.getNoteTemplateById(noteNum);

        if (noteTemplate == null || noteTemplate.getNoteImage() == null) {
            throw new RuntimeException("NoteTemplate 또는 이미지 경로가 존재하지 않습니다.");
        }

        // 다이어리 정보 가져오기
        PersonalDiaryDTO diary = personalDiaryService.getDiaryById(diaryNum);

        // 감정 목록 가져오기
        List<EmotionDTO> emotions = emotionService.getAllEmotions();

        // 공개 권한 목록 가져오기
        List<GrantedDTO> permissions = grantedService.getAllPermissions();

        // 다이어리 카테고리에 맞는 해시태그 목록 가져오기
        Integer categoryNum = diary.getCategoryNum();
        List<HashtagDTO> hashtags = hashtagService.getHashtagsByCategory(categoryNum);

        // 모델에 데이터 추가
        model.addAttribute("noteTemplate", noteTemplate);
        model.addAttribute("diaryNum", diaryNum);
        model.addAttribute("noteName", noteName);
        model.addAttribute("emotions", emotions);
        model.addAttribute("permissions", permissions);
        model.addAttribute("hashtags", hashtags);

        return "personal/NoteForm";
    }

    /**
     * 노트 저장을 위한 POST 메서드
     *
     * @param diaryNum        다이어리 번호
     * @param noteNum         노트 번호
     * @param noteName        노트 이름
     * @param diaryDate       작성 날짜
     * @param diaryEmotion    감정 선택
     * @param diaryPermission 공개 권한 선택
     * @param locationTag     위치 정보
     * @param diaryContent    일기 내용
     * @param hashtags        해시태그 선택
     * @param diaryImage      첨부 이미지 파일
     * @param model           모델 객체
     * @param principal       인증된 사용자 정보
     * @return 저장 결과에 따른 뷰 이름
     */
    @PostMapping("/saveNote")
    public String saveNote(@RequestParam("diaryNum") Integer diaryNum,
                           @RequestParam("noteNum") Integer noteNum,
                           @RequestParam("noteName") String noteName,
                           @RequestParam("diaryDate") String diaryDate,
                           @RequestParam("diaryEmotion") Integer diaryEmotion,
                           @RequestParam("diaryPermission") Integer diaryPermission,
                           @RequestParam("locationTag") String locationTag,
                           @RequestParam("diaryContent") String diaryContent,
                           @RequestParam(value = "hashtags", required = false) List<Integer> hashtags,
                           @RequestParam(value = "diaryImage", required = false) MultipartFile diaryImage,
                           Model model,
                           Principal principal) {
        log.info("Received noteNum: {}", noteNum);
        try {
            // 현재 로그인한 사용자의 ID 가져오기
            String memberId = principal.getName();

            // PersonalNoteDTO 생성 및 값 설정
            PersonalNoteDTO noteDTO = new PersonalNoteDTO();
            noteDTO.setDiaryNum(diaryNum);
            noteDTO.setPersonalNoteNum(noteNum);
            noteDTO.setNoteTitle(noteName); // 노트 이름 설정
            noteDTO.setDiaryDate(Timestamp.valueOf(LocalDate.parse(diaryDate).atStartOfDay()));
            noteDTO.setEmotionNum(diaryEmotion);
            noteDTO.setGrantedNum(diaryPermission);
            noteDTO.setLocation(locationTag);
            noteDTO.setContents(diaryContent);
            noteDTO.setMemberId(memberId);
            noteDTO.setPersonalNoteNum(noteNum); // 노트 템플릿 번호 설정
            noteDTO.setLikeCount(0);
            noteDTO.setViewCount(0);
            noteDTO.setWeather("Sunny"); // 기본 날씨 설정

            // 이미지 파일 처리
            if (diaryImage != null && !diaryImage.isEmpty()) {
                // 업로드된 파일 정보 로그 출력
                log.info("업로드된 파일 이름: {}", diaryImage.getOriginalFilename());
                log.info("파일 크기: {}", diaryImage.getSize());

                // 고유한 파일 이름 생성
                String fileName = UUID.randomUUID().toString() + "_" + diaryImage.getOriginalFilename();

                // 업로드 디렉토리 경로 설정 및 생성
                File uploadDir = new File(this.uploadDir);
                if (!uploadDir.exists()) {
                    boolean dirCreated = uploadDir.mkdirs(); // 디렉토리 생성
                    log.info("업로드 디렉토리 생성 여부: {}", dirCreated);
                }

                // 파일 저장 경로 설정
                File saveFile = new File(uploadDir, fileName);
                log.info("저장될 파일 경로: {}", saveFile.getAbsolutePath());

                // 파일 저장
                diaryImage.transferTo(saveFile);
                noteDTO.setFileName(fileName); // 파일 이름 설정
            }

            // 노트 저장
            personalNoteService.saveNote(noteDTO, hashtags);

            // 성공 시 MyNote 페이지로 리다이렉트
            return "redirect:/personal/MyNote/" + diaryNum;

        } catch (IOException e) {
            log.error("파일 저장 실패 - 경로: {}, 파일 이름: {}", this.uploadDir, diaryImage != null ? diaryImage.getOriginalFilename() : "null", e);
            model.addAttribute("errorMessage", "파일 저장에 실패했습니다. 다시 시도해주세요.");
            // 오류 발생 시 다이어리 작성 페이지로 이동
            return createDiary(noteNum, diaryNum, noteName, model);
        } catch (Exception e) {
            log.error("노트 저장 실패", e);
            model.addAttribute("errorMessage", "노트 저장에 실패했습니다. 다시 시도해주세요.");
            // 오류 발생 시 다이어리 작성 페이지로 이동
            return createDiary(noteNum, diaryNum, noteName, model);
        }
    }

    /**
     * 노트 정보를 가져오는 메서드 (Ajax 호출)
     * @param noteNum 노트 번호
     * @return PersonalNoteDTO
     */
    @GetMapping("/viewNote/{noteNum}")
    @ResponseBody
    public ResponseEntity<PersonalNoteDTO> viewNote(@PathVariable("noteNum") Integer noteNum) {
        PersonalNoteDTO note = personalNoteService.getNoteByNum(noteNum);
        List<String> hashtags = personalNoteService.getHashtagsByNoteNum(noteNum);  // 해시태그 불러오기
        note.setHashtags(hashtags);  // DTO에 해시태그 추가

        if (note == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(note, HttpStatus.OK);
    }
}
