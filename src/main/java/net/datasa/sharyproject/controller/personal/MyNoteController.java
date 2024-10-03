package net.datasa.sharyproject.controller.personal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.EmotionDTO;
import net.datasa.sharyproject.domain.dto.HashtagDTO;
import net.datasa.sharyproject.domain.dto.mypage.ProfileDTO;
import net.datasa.sharyproject.domain.dto.personal.*;
import net.datasa.sharyproject.service.EmotionService;
import net.datasa.sharyproject.service.HashtagService;
import net.datasa.sharyproject.service.mypage.ProfileService;
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
    private final ProfileService profileService; // 프로필 서비스 추가

    // 파일 업로드 경로 설정
    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 노트 템플릿을 기반으로 다이어리 작성 페이지로 이동
     *
     * @param noteNum  노트 번호
     * @param diaryNum 다이어리 번호
     * @param noteName 노트 이름
     * @param model    모델 객체
     * @param principal 현재 로그인한 사용자 정보
     * @return 다이어리 작성 페이지 뷰
     */
    @GetMapping("noteForm")
    public String createDiary(@RequestParam("noteNum") Integer noteNum,
                              @RequestParam("diaryNum") Integer diaryNum,
                              @RequestParam("noteName") String noteName,
                              Model model, Principal principal) {

        // 필요한 파라미터가 제대로 전달되었는지 로그로 확인
        log.info("Received noteNum: {}, diaryNum: {}, noteName: {}", noteNum, diaryNum, noteName);


        // 노트 템플릿 정보 가져오기
        NoteTemplateDTO noteTemplate = noteTemplateService.getNoteTemplateById(noteNum);
        if (noteTemplate == null || noteTemplate.getNoteImage() == null) {
            throw new RuntimeException("노트 템플릿 또는 이미지 경로가 존재하지 않습니다.");
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

        // 현재 로그인한 사용자의 프로필 정보 가져오기
        String memberId = principal.getName();
        ProfileDTO profile = profileService.getProfileByMemberId(memberId); // 프로필 정보 가져오기

        // 모델에 데이터 추가
        model.addAttribute("noteTemplate", noteTemplate);
        model.addAttribute("diaryNum", diaryNum);
        model.addAttribute("noteName", noteName);
        model.addAttribute("emotions", emotions);
        model.addAttribute("permissions", permissions);
        model.addAttribute("hashtags", hashtags);
        model.addAttribute("profile", profile); // 프로필 정보 추가

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
            return createDiary(noteNum, diaryNum, noteName, model, principal);
        } catch (Exception e) {
            log.error("노트 저장 실패", e);
            model.addAttribute("errorMessage", "노트 저장에 실패했습니다. 다시 시도해주세요.");
            // 오류 발생 시 다이어리 작성 페이지로 이동
            return createDiary(noteNum, diaryNum, noteName, model, principal);
        }
    }

    /**
     * 노트 정보를 가져오는 메서드 (Ajax 호출)
     *
     * @param noteNum 노트 번호
     * @return PersonalNoteDTO
     */
    @GetMapping("/viewNote/{noteNum}")
    @ResponseBody
    public ResponseEntity<PersonalNoteDTO> viewNote(@PathVariable("noteNum") Integer noteNum) {
        // 노트 정보를 가져옴
        PersonalNoteDTO note = personalNoteService.getNoteByNum(noteNum);

        // 해시태그 정보를 추가로 가져옴
        List<String> hashtags = personalNoteService.getHashtagsByNoteNum(noteNum);
        note.setHashtags(hashtags);  // DTO에 해시태그 리스트를 추가

        if (note == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // 노트 정보 반환
        return new ResponseEntity<>(note, HttpStatus.OK);
    }

    /**
     * 노트 수정 또는 삭제 페이지로 이동
     *
     * @param noteNum  수정 또는 삭제할 노트 번호
     * @param model    모델 객체
     * @return 수정/삭제 페이지로 이동
     */
    @GetMapping("/editOrDeleteNote")
    public String editOrDeleteNote(@RequestParam("noteNum") Integer noteNum,
                                   Model model, Principal principal) {
        // 노트 정보 가져오기
        PersonalNoteDTO note = personalNoteService.getNoteByNum(noteNum);
        if (note == null) {
            model.addAttribute("errorMessage", "해당 노트를 찾을 수 없습니다.");
            return "error/404";
        }

        // 노트 템플릿 목록 가져오기
        List<NoteTemplateDTO> templates = noteTemplateService.getNoteTemplates();

        // 다이어리 정보 가져오기
        PersonalDiaryDTO diary = personalDiaryService.getDiaryById(note.getDiaryNum());

        // 감정 목록 가져오기
        List<EmotionDTO> emotions = emotionService.getAllEmotions();

        // 공개 권한 목록 가져오기
        List<GrantedDTO> permissions = grantedService.getAllPermissions();

        // 다이어리 카테고리에 맞는 해시태그 목록 가져오기
        Integer categoryNum = diary.getCategoryNum();
        List<HashtagDTO> hashtags = hashtagService.getHashtagsByCategory(categoryNum);

        // 현재 로그인한 사용자의 프로필 정보 가져오기
        String memberId = principal.getName();
        ProfileDTO profile = profileService.getProfileByMemberId(memberId);

        // 모델에 데이터 추가
        model.addAttribute("note", note);
        model.addAttribute("templates", templates);
        model.addAttribute("emotions", emotions);
        model.addAttribute("permissions", permissions);
        model.addAttribute("hashtags", hashtags);
        model.addAttribute("profile", profile); // 프로필 정보 추가

        return "personal/editOrDeleteNote"; // 수정 페이지로 이동
    }

    // 노트 삭제 기능 (DELETE 요청 처리)
    @DeleteMapping("/deleteNote/{noteNum}")
    @ResponseBody
    public ResponseEntity<String> deleteNote(@PathVariable("noteNum") Integer noteNum) {
        try {
            personalNoteService.deleteNoteById(noteNum);
            return ResponseEntity.ok("노트가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("노트 삭제에 실패했습니다.");
        }
    }

    @PostMapping("updateNote")
    public String updateNote(@RequestParam("diaryNum") Integer diaryNum,
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

        log.info("Updating noteNum: {}", noteNum);

        try {
            // 현재 로그인한 사용자의 ID 가져오기
            String memberId = principal.getName();

            // 기존 노트 정보 가져오기
            PersonalNoteDTO existingNote = personalNoteService.getNoteByNum(noteNum);

            // 노트 정보 업데이트
            existingNote.setDiaryNum(diaryNum);
            existingNote.setNoteTitle(noteName); // 노트 이름 업데이트
            existingNote.setDiaryDate(Timestamp.valueOf(LocalDate.parse(diaryDate).atStartOfDay()));
            existingNote.setEmotionNum(diaryEmotion);
            existingNote.setGrantedNum(diaryPermission);
            existingNote.setLocation(locationTag);
            existingNote.setContents(diaryContent);
            existingNote.setMemberId(memberId);

            // 이미지 파일 처리
            if (diaryImage != null && !diaryImage.isEmpty()) {
                // 기존 이미지가 있으면 삭제할 수도 있음 (옵션)
                log.info("새 이미지 업로드 처리 중: {}", diaryImage.getOriginalFilename());

                // 고유한 파일 이름 생성
                String fileName = UUID.randomUUID().toString() + "_" + diaryImage.getOriginalFilename();

                // 업로드 디렉토리 경로 설정 및 생성
                File uploadDir = new File(this.uploadDir);
                if (!uploadDir.exists()) {
                    boolean dirCreated = uploadDir.mkdirs();
                    log.info("업로드 디렉토리 생성 여부: {}", dirCreated);
                }

                // 파일 저장 경로 설정
                File saveFile = new File(uploadDir, fileName);
                diaryImage.transferTo(saveFile);

                // 노트에 새 파일 이름 설정
                existingNote.setFileName(fileName);
            }

            // 해시태그 업데이트
            existingNote.setHashtagNums(hashtags != null ? hashtags : existingNote.getHashtagNums());

            // 노트 정보 업데이트 호출
            personalNoteService.updateNote(existingNote, hashtags);

            // 성공 시 MyNote 페이지로 리다이렉트
            return "redirect:/personal/MyNote/" + diaryNum;

        } catch (IOException e) {
            log.error("파일 저장 실패: {}", diaryImage != null ? diaryImage.getOriginalFilename() : "파일 없음", e);
            model.addAttribute("errorMessage", "파일 저장에 실패했습니다. 다시 시도해주세요.");
            return "personal/editOrDeleteNote";
        } catch (Exception e) {
            log.error("노트 업데이트 실패", e);
            model.addAttribute("errorMessage", "노트 업데이트에 실패했습니다. 다시 시도해주세요.");
            return "personal/editOrDeleteNote";
        }
    }
}